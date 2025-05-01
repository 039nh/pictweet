package in.tech_camp.pictweet.system;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import in.tech_camp.pictweet.PictweetApplication;
import in.tech_camp.pictweet.entity.UserEntity;
import in.tech_camp.pictweet.factory.UserFormFactory;
import in.tech_camp.pictweet.form.UserForm;
import in.tech_camp.pictweet.repository.UserRepository;

@ActiveProfiles("test")
@SpringBootTest(classes = PictweetApplication.class)
@AutoConfigureMockMvc
public class UserRegistrationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;
    
    private UserForm userForm; // フィールドの宣言

    private int initialCount;
    private int afterCount;

    @BeforeEach
    public void setup() {
        // テスト用のユーザー情報をセットアップ
        userForm = UserFormFactory.createUser();
    }

   @Nested
    class ユーザー新規登録ができるとき {

    @Test
    void 正しい情報を入力すればユーザー新規登録ができてトップページに移動する() throws Exception {
        // トップページにアクセス
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("tweets/index"))
                // トップページにサインアップページへ遷移するボタンがあることを確認する
                .andExpect(content().string(org.hamcrest.Matchers.containsString("新規登録")));
        // 新規登録ページへ移動する
        mockMvc.perform(get("/users/sign_up"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/signUp"));

        List<UserEntity> userBeforeDeletion = userRepository.findAll();
        initialCount = userBeforeDeletion.size();

        // 新規登録情報を送信
        mockMvc.perform(post("/user")
                // ユーザー情報を入力する
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("nickname", userForm.getNickname())
                .param("email", userForm.getEmail())
                .param("password", userForm.getPassword())
                .param("passwordConfirmation", userForm.getPasswordConfirmation())
                .with(csrf())) // CSRFトークンを含める
                // トップページへ遷移することを確認する
                .andExpect(redirectedUrl("/"))
                .andExpect(status().isFound());
        // 新規登録に成功するとユーザーテーブルのカウントが1上がる
        List<UserEntity> userAfterDeletion = userRepository.findAll(); // findAll()メソッドを呼び出してすべてのユーザーを取得
        afterCount = userAfterDeletion.size(); // 取得したユーザー数を計算
        assertEquals(initialCount + 1, afterCount);
    }

    @Test
    void 誤った情報ではユーザー新規登録ができずに新規登録ページへ戻ってくる() throws Exception {
        // トップページに移動する
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("tweets/index")) // トップページのビューを確認
                // トップページにサインアップページへ遷移するボタンがあることを確認する
                .andExpect(content().string(org.hamcrest.Matchers.containsString("新規登録"))); // サインアップボタンが存在するかチェック
        // 新規登録ページへ移動する
        mockMvc.perform(get("/users/sign_up"))
                .andExpect(status().isOk())
                .andExpect(view().name("users/signUp"));
        // ユーザー情報を入力する
        List<UserEntity> userBeforeDeletion = userRepository.findAll();
        initialCount = userBeforeDeletion.size();
        // 新規登録ページへ戻されることを確認する
        mockMvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("nickname", "")
                .param("email", userForm.getEmail())
                .param("password", userForm.getPassword())
                .param("passwordConfirmation", userForm.getPasswordConfirmation())
                .with(csrf())) // CSRFトークンを含める
                .andExpect(view().name("users/signUp")) // 新規登録ページに戻ることを確認
                .andExpect(status().isOk()); // 新規登録ページが表示されることを確認
        // 新規登録に失敗したらユーザーテーブルのカウントは上がらない
        List<UserEntity> userAfterDeletion = userRepository.findAll();
        afterCount = userAfterDeletion.size();
        assertEquals(initialCount, afterCount);
    }
  }
}
