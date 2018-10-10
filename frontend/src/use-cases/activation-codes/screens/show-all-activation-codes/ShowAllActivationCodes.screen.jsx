import React from "react";
import GammaTable from "../../../../common/views/gamma-table";
import { Fill } from "../../../../common-ui/layout";
import translations from "./ShowAllActivationCodes.screen.translations.json";
import GammaTranslations from "../../../../common/declaratives/gamma-translations";

const ShowAllActivationCodes = ({ activationCodes, text }) => (
  <Fill>
    <GammaTranslations
      translations={translations}
      uniquePath="ActivationCodes.Screen.ShowAllActivationCodes"
      render={text => (
        <GammaTable
          titleText={text.ActivationCodes}
          searchText={text.SearchForActivationCodes}
          idProp="id"
          startOrderBy="cid"
          columnsOrder={["id", "cid", "code", "createdAt"]}
          headerTexts={{
            id: text.Id,
            cid: text.Cid,
            code: text.Code,
            createdAt: text.CreatedAt,
            __link: text.Details
          }}
          data={activationCodes.map(activationCode => {
            return {
              ...activationCode,
              __link: "/activation-codes/" + activationCode.id
            };
          })}
          emptyTableText={text.NoActivationCodes}
        />
      )}
    />
  </Fill>
);

export default ShowAllActivationCodes;
