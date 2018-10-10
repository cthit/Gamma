import React from "react";
import styled from "styled-components";

import { Spacing, Padding, Fill, Center } from "../../../common-ui/layout";
import { Title, Subtitle } from "../../../common-ui/text";
import IfElseRendering from "../../../common/declaratives/if-else-rendering";
import GammaButton from "../../../common/elements/gamma-button";
import GammaTranslations from "../../../common/declaratives/gamma-translations";

const UserInformation = ({
  loaded,
  loggedIn,
  user,
  logout,
  currentPath,
  toastOpen
}) => (
  <IfElseRendering
    test={loaded == null ? false : loaded && loggedIn}
    ifRender={() => (
      <GammaTranslations
        onlyCommon
        render={text => (
          <Container>
            <Center>
              <Title white text={user.nick} />
            </Center>
            <Spacing />
            <GammaButton
              raised
              text={text.Logout}
              onClick={() => logout(text.LoggedOut)}
            />
          </Container>
        )}
      />
    )}
  />
);

const Container = styled.div`
  display: flex;
  flex-direction: row;
  justify-content: center;
`;

export default UserInformation;
