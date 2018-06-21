import React from "react";
import styled from "styled-components";
import { Spacing, Padding } from "../../common-ui/layout";
import { HeadingLevel2, HeadingLevel3 } from "../../common-ui/text";

export const UserInformation = ({
  userData = { nick: "Portals", firstName: "Sven", lastName: "Svensson" },
  signedIn = true
}) => (
  <Padding>
    <HeadingLevel2>{userData.nick}</HeadingLevel2>
    <Spacing />
    <HeadingLevel3>
      {userData.firstName + " - " + userData.lastName}
    </HeadingLevel3>
    <Spacing />
    <ProfileImage src="/digit18.png" />
  </Padding>
);

export const ProfileImage = styled.img`
  width: 231px;
  height: 231px;
  object-fit: cover;
`;
