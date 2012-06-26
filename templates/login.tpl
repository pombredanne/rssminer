<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <title>Login - Rssminer, an intelligent RSS reader</title>
    <link rel="stylesheet" href="/css/landing.css?{VERSION}">
    <meta name="description"
          content="Rssminer is a another Web-based aggregator, let you
                   read up to date Atom and RSS feeds online in one
                   place with ease. We Focused on reading experience!">
  </head>
  <body>
    <div id="accouts-div">
      <h1><a href="/"> Rssminer</a> Login </h1>
      {{#msg}}<p id="error">{{{ msg }}}</p>{{/msg}}
      <form action="/login" method="post" class="post-form">
        <table>
          <tr>
            <td><label for="email">Email:</label>
            </td>
            <td><input class="textfield" name="email" id="email" />
              <span class="required">*</span>
            </td>
          </tr>
          <tr>
            <td><label for="password">Password:</label>
            </td>
            <td><input class="textfield" name="password"
                       id="password" type="password"/>
              <span class="required">*</span>
            </td>
          </tr>
          <tr>
            <td></td>
            <td>
              <input class="submit" type="submit" value="Login" />
            </td>
          </tr>
        </table>
        <input value="{{{return_url}}}" name="return-url" type="hidden" />
      </form>
      <div class="openid">
        <h3>Already have an Google account?</h3>
        <a href="/login/google">
          <img src="/imgs/openid_google.png"/>
        </a>
      </div>
    </div>
  </body>
</html>



