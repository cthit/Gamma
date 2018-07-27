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

const ShowGroupDetails = ({ group }) => (
  <IfElseRendering
    test={group != null}
    ifRender={() => (
      <Fill>
        <Center>
          <GammaCard minWidth="300px" maxWidth="600px">
            <GammaCardBody>
              <GammaDisplayData
                data={{
                  name: group.name,
                  description_sv: group.description.sv,
                  description_en: group.description.en,
                  email: group.email,
                  func_sv: group.func.sv,
                  func_en: group.func.en
                }}
                keysText={{
                  name: "Namn",
                  description_sv: "Description Sv",
                  description_en: "Description EN",
                  email: "Email",
                  func_sv: "Func SV",
                  func_en: "Func EN"
                }}
                keysOrder={[
                  "name",
                  "description_sv",
                  "description_en",
                  "email",
                  "func_sv",
                  "func_en"
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
);

export default ShowGroupDetails;
