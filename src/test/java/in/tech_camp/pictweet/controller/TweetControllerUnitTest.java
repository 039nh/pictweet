package in.tech_camp.pictweet.controller;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import in.tech_camp.pictweet.entity.TweetEntity;
import in.tech_camp.pictweet.form.SearchForm;
import in.tech_camp.pictweet.repository.TweetRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TweetControllerUnitTest {
  @Mock
  private TweetRepository tweetRepository;

  @InjectMocks
  private TweetController tweetController;

  private Model model;

  @BeforeEach
  public void setUp() { 
    model = new ExtendedModelMap(); // モデルオブジェクトのインスタンス形成
  }

  @Test
  public void 投稿一覧機能にリクエストするとツイート一覧表示のビューファイルがレスポンスで返ってくる() {

    String result = tweetController.showTweets(model); // showTweetsメソッドを呼び出している

    // アサーション
    assertThat(result,is("tweets/index")); // resultが期待される値である"tweets/index"と等しいかどうかを確認
  }

  @Test
    public void 投稿一覧機能にリクエストするとレスポンスに投稿済みのツイートがすべて含まれること() {
      // ツイートエンティティを用意
        TweetEntity tweet1 = new TweetEntity();
        tweet1.setId(1);
        tweet1.setText("ツイート1");
        tweet1.setImage("image1.jpg");

        TweetEntity tweet2 = new TweetEntity();
        tweet2.setId(2);
        tweet2.setText("ツイート2");
        tweet2.setImage("image2.jpg");

        // expectedTweetListとして保存
        List<TweetEntity> expectedTweetList = Arrays.asList(tweet1, tweet2);

        // findAllメソッドが呼び出されたときの振る舞いを定義
        when(tweetRepository.findAll()).thenReturn(expectedTweetList);

        tweetController.showTweets(model);

        assertThat(model.getAttribute("tweets"), is(expectedTweetList));
    }

    @Test
    public void 投稿一覧機能にリクエストするとレスポンスに投稿検索フォームが存在する() {
        SearchForm searchForm = new SearchForm(); // 新しいSearchFormオブジェクトを作成
        tweetController.showTweets(model); // コントローラーのshowTweetsメソッドを呼び出す
        // モデルオブジェクトから検索フォームを取得し、期待されるformと比較
        assertThat(model.getAttribute("searchForm"), is(searchForm));
    }
}
