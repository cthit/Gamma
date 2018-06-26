import React from "react";
import styled from "styled-components";

import { Spacing, Padding } from "../../../common-ui/layout";
import {
  HeadingLevel2,
  HeadingLevel3,
  Title,
  Subtitle
} from "../../../common-ui/text";

const UserInformation = ({
  userData = { nick: "Portals", firstName: "Sven", lastName: "Svensson" },
  signedIn = true
}) => (
  <Padding>
    <Title text={userData.nick} />
    <Spacing />
    <Subtitle text={userData.firstName + " - " + userData.lastName} />
    <Spacing />
    <ProfileImage src="/digit18.png" />
  </Padding>
);

export const ProfileImage = styled.img`
  width: 231px;
  height: 231px;
  object-fit: cover;
`;

export default UserInformation;
