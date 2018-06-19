import React from "react";
import styled from "styled-components";
import { Spacing, Padding } from "../../common-ui/layout";

export const UserInformation = ({
  userData = { nick: "Portals", firstName: "Sven", lastName: "Svensson" },
  signedIn = true
}) => (
  <Padding>
    <ProfileHeader>{userData.nick}</ProfileHeader>
    <Spacing />
    <ProfileSubHeader>
      {userData.firstName + " - " + userData.lastName}
    </ProfileSubHeader>
    <Spacing />
    <ProfileImage src="/digit18.png" />
  </Padding>
);

export const ProfileHeader = styled.p`
  font-size: 20px;
  margin: 0px;
`;
export const ProfileSubHeader = styled.p`
  font-size: 18px;
  margin: 0px;
`;
export const ProfileImage = styled.img`
  width: 231px;
  height: 231px;
  object-fit: cover;
`;
