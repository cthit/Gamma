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
import translations from "./ShowActivationCodeDetails.screen.translations.json";

const ShowActivationCodeDetails = ({ activationCode }) => (
  <IfElseRendering
    test={activationCode != null}
    ifRender={() => (
      <GammaTranslations
        translations={translations}
        uniquePath="ActivationCode.Screen.ShowActivationCodeDetails"
        render={text => (
          <Fill>
            <Center>
              <GammaCard minWidth="300px" maxWidth="600px">
                <GammaCardBody>
                  <GammaDisplayData
                    data={activationCode}
                    keysText={{
                      id: text.Id,
                      cid: text.Cid,
                      code: text.Code,
                      createdAt: text.CreatedAt
                    }}
                    keysOrder={["id", "cid", "code", "createdAt"]}
                  />
                </GammaCardBody>
              </GammaCard>
            </Center>
            <GammaLink to={"/activation-codes/" + activationCode.id + "/edit"}>
              <GammaFABButton component={Edit} secondary />
            </GammaLink>
          </Fill>
        )}
      />
    )}
  />
);

export default ShowActivationCodeDetails;
