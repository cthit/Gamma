import { DigitTable, DigitTranslations } from "@cthit/react-digit-components";
import React from "react";
import translations from "./Gdpr.translations.json";

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
      <DigitTranslations
        translations={translations}
        uniquePath="Gdpr"
        render={text => (
          <DigitTable
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
