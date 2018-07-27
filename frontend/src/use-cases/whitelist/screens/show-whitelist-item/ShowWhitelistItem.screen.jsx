import React from "react";
import styled from "styled-components";
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
import GammaDisplayData from "../../../../common/elements/gamma-display-data";
import { Edit } from "@material-ui/icons";

const ShowWhitelistItem = ({ whitelistItem, text }) => (
  <IfElseRendering
    test={whitelistItem != null}
    ifRender={() => (
      <Fill>
        <Center>
          <GammaCard minWidth="300px" maxWidth="600px">
            <GammaCardBody>
              <GammaDisplayData
                data={whitelistItem}
                keysOrder={["id", "cid"]}
                keysText={{ id: text.id, cid: text.cid }}
              />
            </GammaCardBody>
          </GammaCard>
        </Center>
        <GammaLink to={"/whitelist/" + whitelistItem.id + "/edit"}>
          <GammaFABButton component={Edit} secondary />
        </GammaLink>
      </Fill>
    )}
  />
);

export default ShowWhitelistItem;
