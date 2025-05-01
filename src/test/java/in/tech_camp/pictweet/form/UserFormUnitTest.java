package in.tech_camp.pictweet.form;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindingResult;

import in.tech_camp.pictweet.factory.UserFormFactory;
import in.tech_camp.pictweet.validation.ValidationPriority1;
import in.tech_camp.pictweet.validation.ValidationPriority2;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

// テスト環境用の設定
@ActiveProfiles("test")
public class UserFormUnitTest {
  private UserForm userForm; // UserFormオブジェクトを保持するフィールド
  private Validator validator; // Validatorインスタンスを保持するフィールド
  private BindingResult bindingResult;

  @BeforeEach
    public void setUp() {
      // Validatorインスタンスを初期化し、UserFormオブジェクトを作成
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator(); // インスタンスを取得
        userForm = UserFormFactory.createUser(); // 新しいUserFormオブジェクトを作成
        bindingResult = Mockito.mock(BindingResult.class);
    }

  @Nested
  class ユーザー作成ができる場合 {
    @Test
    public void nicknameとemailとpasswordとpasswordConfirmationが存在すれば登録できる() {
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
      assertEquals(0, violations.size()); // エラー情報の数が0であれば、エラーがない
    }
  }

@Nested
class ユーザー作成ができない場合 {
    @Test
    public void nicknameが空の場合バリデーションエラーが発生する() {
        userForm.setNickname(""); // nicknameを空に設定
        Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class); // 違反セットを取得
        //  アサーション
        assertEquals(1, violations.size());
        assertEquals("Nickname can't be blank", violations.iterator().next().getMessage());
  }

    @Test
    public void emailが空の場合バリデーションエラーが発生する() {
      userForm.setEmail("");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
      assertEquals(1, violations.size());
      assertEquals("Email can't be blank", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordが空の場合バリデーションエラーが発生する() {
      userForm.setPassword("");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority1.class);
      assertEquals(1, violations.size());
      assertEquals("Password can't be blank", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordとpasswordConfirmationが不一致ではバリデーションエラーが発生する() {
      //  異なるパスワードを設定
      userForm.setPasswordConfirmation("differentPassword"); // インスタンスデータを用意
      //  バリデーションメソッドを呼び出す
      userForm.validatePasswordConfirmation(bindingResult);
      //  結果とエラーメッセージの一致を確認
      verify(bindingResult).rejectValue("passwordConfirmation", "error.user", "Password confirmation doesn't match Password");
    }

    @Test
    public void nicknameが7文字以上ではバリデーションエラーが発生する() {
      userForm.setNickname("TooLong");
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority2.class);
      assertEquals(1, violations.size());
      assertEquals("Nickname is too long (maximum is 6 characters)", violations.iterator().next().getMessage());
    }

    @Test
    public void emailはアットマークを含まないとバリデーションエラーが発生する() {
      userForm.setEmail("invalidEmail"); // 無効なメール
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority2.class);
      assertEquals(1, violations.size());
      assertEquals("Email should be valid", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordが5文字以下ではバリデーションエラーが発生する() {
      String password = "a".repeat(5);
      userForm.setPassword(password);
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority2.class);
      violations.forEach(violation -> System.out.println(violation.getMessage())); // バリデーションエラーのメッセージをコンソールに表示する
      assertEquals(1, violations.size());
      assertEquals("Password should be between 6 and 128 characters", violations.iterator().next().getMessage());
    }

    @Test
    public void passwordが129文字以上ではバリデーションエラーが発生する() {
      String password = "a".repeat(129); // 129文字のaを繰り返した文字列を作成
      userForm.setPassword(password);
      Set<ConstraintViolation<UserForm>> violations = validator.validate(userForm, ValidationPriority2.class);
      violations.forEach(violation -> System.out.println(violation.getMessage()));
      assertEquals(1, violations.size());
      assertEquals("Password should be between 6 and 128 characters", violations.iterator().next().getMessage());
    }
  }
}
