// Factoryクラスを使用したインスタンス生成の共通化

package in.tech_camp.pictweet.factory;

import com.github.javafaker.Faker;

import in.tech_camp.pictweet.form.UserForm;

public class UserFormFactory {
  private static final Faker faker = new Faker(); // Fakerインスタンスを生成

  public static UserForm createUser() {
    UserForm userForm = new UserForm();

    userForm.setEmail(faker.internet().emailAddress()); // ランダムなメールアドレスを生成
    String generatedUsername = faker.name().username(); // ランダムなユーザー名を生成しgeneratedUsernameという文字列変数に格納

    // 6文字を超える場合6文字以内に切り詰める
    if (generatedUsername.length() > 6) {
      generatedUsername = generatedUsername.substring(0, 6);
  }
    // 各フィールドに値を設定
    userForm.setNickname(generatedUsername);
    userForm.setPassword(faker.internet().password(6, 12));
    userForm.setPasswordConfirmation(userForm.getPassword());
    return userForm;
  }
}