import React from "react";
import { Fill, Center, Spacing } from "../../../../common-ui/layout";

import {
  GammaCard,
  GammaCardTitle,
  GammaCardBody,
  GammaLink
} from "../../../../common-ui/design";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import { Text } from "../../../../common-ui/text";
import GammaFABButton from "../../../../common/elements/gamma-fab-button";
import { Edit } from "@material-ui/icons";
import GammaDisplayData from "../../../../common/elements/gamma-display-data/GammaDisplayData.element";
import GammaTranslations from "../../../../common/declaratives/gamma-translations";
import translations from "./ShowGroupDetails.screen.translations.json";

const ShowGroupDetails = ({ group }) => (
  <IfElseRendering
    test={group != null}
    ifRender={() => (
      <GammaTranslations
        translations={translations}
        uniquePath="Groups.Screen.ShowGroupDetails"
        render={text => (
          <Fill>
            <Center>
              <GammaCard minWidth="300px" maxWidth="600px">
                <GammaCardBody>
                  <GammaDisplayData
                    data={{
                      id: group.id,
                      name: group.name,
                      description_sv: group.description.sv,
                      description_en: group.description.en,
                      email: group.email,
                      func_sv: group.func.sv,
                      func_en: group.func.en,
                      type: _getTypeText(group.type, text)
                    }}
                    keysText={{
                      id: text.Id,
                      name: text.Name,
                      description_sv: text.DescriptionSv,
                      description_en: text.DescriptionEn,
                      email: text.Email,
                      func_sv: text.FunctionSv,
                      func_en: text.FunctionEn,
                      type: _getTypeText(group.type, text)
                    }}
                    keysOrder={[
                      "id",
                      "name",
                      "description_sv",
                      "description_en",
                      "email",
                      "func_sv",
                      "func_en",
                      "type"
                    ]}
                  />
                </GammaCardBody>
              </GammaCard>
            </Center>
            <GammaLink to={"/groups/" + group.id + "/edit"}>
              <GammaFABButton component={Edit} secondary />
            </GammaLink>
          </Fill>
        )}
      />
    )}
  />
);

function _getTypeText(type, text) {
  switch (type) {
    case "SOCIETY":
      return text.society;
    case "COMMITTEE":
      return text.Committee;
    case "BOARD":
      return text.Board;
    default:
      return "Unknown";
  }
}

export default ShowGroupDetails;
