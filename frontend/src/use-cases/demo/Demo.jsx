import React from "react";
import translations from "./Demo.translations.json";
import GammaTranslations from "../../common/declaratives/gamma-translations";

class Demo extends React.Component {
  render() {
    return (
      <div>
        <GammaTranslations
          translations={translations}
          baseUrl="Demo"
          render={text => <h1>{console.log(text)}</h1>}
        />
      </div>
    );
  }
}

export default Demo;
