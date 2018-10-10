import React from "react";

import translations from "./Gdpr.translations.json";

import GammaTranslations from "../../common/declaratives/gamma-translations";
import GammaTable from "../../common/views/gamma-table";

class Gdpr extends React.Component {
  constructor(props) {
    super();

    props.usersLoad().then(response => {
      props.gammaLoadingFinished();
    });
  }

  render() {
    const { users } = this.props;

    return (
      <GammaTranslations
        translations={translations}
        uniquePath="Gdpr"
        render={text => (
          <GammaTable
            titleText="Användare"
            searchText="Sök efter användare"
            idProp="cid"
            startOrderBy="firstName"
            columnsOrder={["cid", "firstName", "nick", "lastName"]}
            headerTexts={{
              cid: "Cid",
              firstName: "firstName",
              lastName: "lastName",
              nick: "nick",
              __switch: "Switch"
            }}
            data={users.map(user => {
              return {};
            })}
          />
        )}
      />
    );
  }
}

export default Gdpr;
