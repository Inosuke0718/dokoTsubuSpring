# `/` で `index.jsp` を表示するための修正理由

## 発生していた問題

`http://localhost:8080/` にアクセスすると、以下のような 404 エラーが発生していました。

```text
Whitelabel Error Page
There was an unexpected error (type=Not Found, status=404).
```

このエラーは、Spring Boot アプリケーションが `/` へのリクエストをどの画面に渡せばよいか判断できなかったために発生していました。

## 原因

このプロジェクトは Spring Boot の組み込み Tomcat で起動する構成です。

そのため、従来の Servlet / JSP アプリケーションのように `web.xml` の `welcome-file-list` だけで `src/main/webapp/index.jsp` が自動表示されるとは限りません。

また、Spring MVC の画面遷移では Controller がリクエストを受け取り、どの JSP に転送するかを明示する必要があります。

## `IndexController` が必要な理由

`/` にアクセスされたときに `index.jsp` を表示するため、以下の Controller を追加しました。

```java
@Controller
public class IndexController {
  @GetMapping("/")
  public String index() {
    return "forward:/index.jsp";
  }
}
```

この Controller により、`http://localhost:8080/` へのアクセスが `src/main/webapp/index.jsp` に転送されます。

`forward:/index.jsp` としている理由は、今回の要望が `src/main/webapp/index.jsp` を表示することだからです。

## `tomcat-embed-jasper` が必要な理由

Spring Boot の組み込み Tomcat で JSP を実行するには、JSP をコンパイル・実行するための Jasper が必要です。

そのため、`pom.xml` に以下の依存関係を追加しました。

```xml
<dependency>
  <groupId>org.apache.tomcat.embed</groupId>
  <artifactId>tomcat-embed-jasper</artifactId>
</dependency>
```

これがない場合、JSP が正しく実行されず、静的リソースとして探されて 404 になることがあります。

## JSTL の依存関係が必要な理由

`WEB-INF/jsp/main.jsp` や `WEB-INF/jsp/loginResult.jsp` では、以下のように JSTL を使用しています。

```jsp
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
```

そのため、JSTL 用の依存関係も `pom.xml` に追加しました。

```xml
<dependency>
  <groupId>jakarta.servlet.jsp.jstl</groupId>
  <artifactId>jakarta.servlet.jsp.jstl-api</artifactId>
</dependency>
<dependency>
  <groupId>org.glassfish.web</groupId>
  <artifactId>jakarta.servlet.jsp.jstl</artifactId>
</dependency>
```

これにより、`<c:out>`、`<c:if>`、`<c:choose>`、`<c:forEach>` などの JSTL タグが実行できるようになります。

## `application.properties` の設定が必要な理由

`application.properties` には以下の設定があります。

```properties
spring.mvc.view.prefix=/WEB-INF/jsp/
spring.mvc.view.suffix=.jsp
```

これは、Controller が `return "main";` や `return "loginResult";` のようにビュー名を返したときに、実際の JSP ファイルへ変換するための設定です。

例:

```text
return "main";
```

は、以下の JSP を表示します。

```text
/WEB-INF/jsp/main.jsp
```

そのため、`MainController`、`LoginController`、`LogoutController` で `WEB-INF/jsp` 配下の JSP を表示するには、この設定が必要です。

## `web.xml` が今回の解決に使われない理由

`src/main/webapp/WEB-INF/web.xml` には `welcome-file-list` が定義されています。

しかし、このプロジェクトは Spring Boot の `main()` メソッドから組み込み Tomcat で起動する構成です。

今回の `/` へのアクセスは Spring MVC の Controller で処理する方が確実です。

そのため、`web.xml` の `welcome-file-list` に頼らず、`IndexController` で明示的に `/` を処理するようにしました。

## まとめ

今回の修正で必要だったことは以下です。

1. `/` を受け取る Controller を追加する
2. `src/main/webapp/index.jsp` に forward する
3. Spring Boot で JSP を実行できるように `tomcat-embed-jasper` を追加する
4. JSTL タグを使うための依存関係を追加する
5. `WEB-INF/jsp` 配下の JSP 用に `application.properties` の view resolver 設定を維持する

これにより、`http://localhost:8080/` にアクセスしたときに `src/main/webapp/index.jsp` が表示されるようになります。
