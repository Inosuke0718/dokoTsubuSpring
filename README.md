# Spring Boot版 どこつぶ (`dokoTsubuSpring`)

## 概要
公式が出しているソースコード:
https://github.com/miyabilink/sukkiri-servlet5-codes/tree/main/dokoTsubuSpring

## 「どこつぶ」作成・修正ファイル一覧

Chapter 15で「どこつぶ」をSpring Bootプロジェクト（`dokoTsubuSpring`）として作り直す際に、**修正（以前のコードをベースに書き換え）または新規作成するファイル**を以下の表にまとめました。

今回の章では、これまでの「ロジック（Logic）」を「サービス（Service）」へ、また「サーブレット」を「コントローラ」へと、Spring Frameworkの慣習に合わせて役割や名前を整えながら開発を進めます。
また、教科書通りの修正点に加えて、Spring Boot環境（組み込みTomcat）でJSPを正しく動作させ、`/` アクセス時に `index.jsp` を表示するための追加設定も含んでいます。

| カテゴリ         | ファイル名                  | 変更内容・ポイント                                                                                           | 参照箇所    |
| :--------------- | :-------------------------- | :----------------------------------------------------------------------------------------------------------- | :---------- |
| **モデル**       | `User.java`                 | **Lombok**を導入し、getterやコンストラクタを自動生成するように修正。                                         | コード15-17 |
|                  | `Mutter.java`               | **Lombok**を導入。一部手書きのコンストラクタを維持。                                                         | コード15-18 |
| **サービス**     | `LoginService.java`         | `LoginLogic`から改名。**`@Service`**を付与。                                                                 | コード15-19 |
|                  | `PostMutterService.java`    | `PostMutterListLogic`から改名。**`@Service`**を付与。                                                        | コード15-20 |
|                  | `GetMutterListService.java` | `GetMutterListLogic`から改名。**`@Service`**を付与。                                                         | コード15-21 |
| **コントローラ** | `LoginController.java`      | サーブレット`Login`を書き換え。**`@Controller`**、**DI（依存性の注入）**、引数でのセッション取得などを適用。 | コード15-22 |
|                  | `LogoutController.java`     | サーブレット`Logout`を書き換え。                                                                             | コード15-23 |
|                  | `MainController.java`       | サーブレット`Main`を書き換え。2つのサービスを同時にDI。未ログイン時はリダイレクト。                          | コード15-24 |
|                  | `IndexController.java`      | **【※教科書での修正指示がない】**`/` へのアクセスを `src/main/webapp/index.jsp` にフォワードするためのControllerを新規作成。      | -           |
| **ビュー**       | `index.jsp`                 | 配置場所を `src/main/webapp` に設定。                                                                        | 表15-4      |
|                  | `logout.jsp`                | 配置場所を `src/main/webapp/WEB-INF/jsp` に設定。                                                            | 表15-4      |
|                  | `main.jsp`                  | 同上                                                                                                         | 表15-4      |
|                  | `loginResult.jsp`           | 同上                                                                                                         | 表15-4      |
| **設定/その他** | `pom.xml`                   | **【※教科書での修正指示がない】**組み込みTomcatでJSPを実行するための`tomcat-embed-jasper`と、JSTLを使用するための依存関係を※教科書での修正指示がない。 | -           |
|                  | `application.properties`    | **【※教科書での修正指示がない】**Controllerが返すビュー名を`WEB-INF/jsp/`配下のJSPファイルにマッピングするビューリゾルバ設定を追加。| -           |

---

## 教科書に載っている以外で修正が必要なところ（詳細な理由）

Spring Boot アプリケーションとして起動（組み込み Tomcat で起動）するにあたり、Servlet/JSP 時代の `web.xml` の `welcome-file-list` などが自動では機能しないため、上記の【追加】部分の対応を行っています。

### 1. `IndexController` が必要な理由
`http://localhost:8080/` にアクセスすると、Spring Boot アプリケーションが `/` へのリクエストをどの画面に渡せばよいか判断できず、以下のような 404 エラー（Whitelabel Error Page）が発生していました。

これを回避し、リクエストを `src/main/webapp/index.jsp` に転送するため、以下の Controller を追加しました。

```java
@Controller
public class IndexController {
  @GetMapping("/")
  public String index() {
    return "forward:/index.jsp";
  }
}
```

### 2. `pom.xml` に依存関係を追加する理由
Spring Boot の組み込み Tomcat で JSP を実行・コンパイルするためには Jasper (`tomcat-embed-jasper`) が必要です。これがない場合、JSP が正しく実行されず、静的リソースとして探されて 404 になることがあります。
また、`WEB-INF/jsp/main.jsp` などで `<c:out>` や `<c:if>` などの JSTL タグを実行するため、JSTL 用の依存関係も必要です。

```xml
<!-- JSP実行用 -->
<dependency>
  <groupId>org.apache.tomcat.embed</groupId>
  <artifactId>tomcat-embed-jasper</artifactId>
</dependency>

<!-- JSTL用 -->
<dependency>
  <groupId>jakarta.servlet.jsp.jstl</groupId>
  <artifactId>jakarta.servlet.jsp.jstl-api</artifactId>
</dependency>
<dependency>
  <groupId>org.glassfish.web</groupId>
  <artifactId>jakarta.servlet.jsp.jstl</artifactId>
</dependency>
```

### 3. `application.properties` の設定が必要な理由
Controller が `return "main";` や `return "loginResult";` のようにビュー名を返した際に、実際の JSP ファイルへ変換するための設定です。

```properties
spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp
```
この設定により、例えば `return "main";` が `/WEB-INF/jsp/main.jsp` として処理されるようになります。

*(備考: `src/main/webapp/WEB-INF/web.xml` や `src/main/webapp/META-INF/MANIFEST.MF` は自動生成される場合がありますが、今回のルーティングや設定には使用しません)*
