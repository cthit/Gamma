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
import translations from "./ShowPostDetails.screen.translations.json";

const ShowPostDetails = ({ post }) => (
  <IfElseRendering
    test={post != null}
    ifRender={() => (
      <GammaTranslations
        translations={translations}
        uniquePath="Posts.Screen.ShowPostDetails"
        render={text => (
          <Fill>
            <Center>
              <GammaCard minWidth="300px" maxWidth="600px">
                <GammaCardBody>
                  <GammaDisplayData
                    data={post}
                    keysText={{ id: text.Id, sv: text.Sv, en: text.En }}
                    keysOrder={["id", "sv", "en"]}
                  />
                </GammaCardBody>
              </GammaCard>
            </Center>
            <GammaLink to={"/posts/" + post.id + "/edit"}>
              <GammaFABButton component={Edit} secondary />
            </GammaLink>
          </Fill>
        )}
      />
    )}
  />
);

export default ShowPostDetails;
