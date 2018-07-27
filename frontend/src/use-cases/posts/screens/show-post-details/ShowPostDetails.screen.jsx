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

const ShowPostDetails = ({ post, text }) => (
  <IfElseRendering
    test={post != null}
    ifRender={() => (
      <Fill>
        <Center>
          <GammaCard minWidth="300px" maxWidth="600px">
            <GammaCardBody>
              <GammaDisplayData
                data={post}
                keysText={{ id: text.id, sv: text.sv, en: text.en }}
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
);

export default ShowPostDetails;
