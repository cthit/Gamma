import React from "react";
import { Fill, Center, Spacing } from "../../../../common-ui/layout";

import {
  GammaCard,
  GammaCardTitle,
  GammaCardBody,
  GammaLink,
  GammaCardButtons
} from "../../../../common-ui/design";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import { Text } from "../../../../common-ui/text";
import GammaButton from "../../../../common/elements/gamma-button";
import { Edit } from "@material-ui/icons";
import GammaDisplayData from "../../../../common/elements/gamma-display-data/GammaDisplayData.element";
import GammaTranslations from "../../../../common/declaratives/gamma-translations";
import translations from "./ShowGroupDetails.screen.translations.json";

const ShowGroupDetails = ({
  group,
  groupsDelete,
  gammaDialogOpen,
  toastOpen,
  redirectTo
}) => (
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
                <GammaCardButtons reverseDirection>
                  <GammaLink to={"/groups/" + group.id + "/edit"}>
                    <GammaButton primary raised text="Redigera" />
                  </GammaLink>
                  <GammaButton
                    text="Radera"
                    onClick={() => {
                      gammaDialogOpen({
                        title: "Radera?",
                        confirmButtonText: "Radera",
                        cancelButtonText: text.Cancel,
                        onConfirm: () => {
                          groupsDelete(group.id)
                            .then(response => {
                              toastOpen({
                                text: "Du har raderat"
                              });

                              redirectTo("/groups");
                            })
                            .catch(error => {
                              toastOpen({
                                text: text.SomethingWentWrong
                              });
                            });
                        }
                      });
                    }}
                  />
                </GammaCardButtons>
              </GammaCard>
            </Center>
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
