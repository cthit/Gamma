import React from "react";
import { Link } from "react-router-dom";
import styled from "styled-components";

import { DigitText } from "@cthit/react-digit-components";

const NoStyleLink = styled(Link)`
    color: black;
`;

const HomeLink = ({ text, link }) => (
    <NoStyleLink to={link}>
        <DigitText.Text text={text} />
    </NoStyleLink>
);

export default HomeLink;
