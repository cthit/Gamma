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
import translations from "./ShowWebsiteDetails.screen.translations.json";

const ShowWebsiteDetails = ({ website }) => (
  <IfElseRendering
    test={website != null}
    ifRender={() => (
      <GammaTranslations
        translations={translations}
        uniquePath="Websites.Screen.ShowWebsiteDetails"
        render={text => (
          <Fill>
            <Center>
              <GammaCard minWidth="300px" maxWidth="600px">
                <GammaCardBody>
                  <GammaDisplayData
                    data={website}
                    keysText={{
                      id: text.Id,
                      name: text.Name,
                      prettyName: text.PrettyName
                    }}
                    keysOrder={["id", "name", "prettyName"]}
                  />
                </GammaCardBody>
              </GammaCard>
            </Center>
            <GammaLink to={"/websites/" + website.id + "/edit"}>
              <GammaFABButton component={Edit} secondary />
            </GammaLink>
          </Fill>
        )}
      />
    )}
  />
);

export default ShowWebsiteDetails;
