import React from "react";
import styled from "styled-components";

import { DigitText } from "@cthit/react-digit-components";
import { Link } from "react-router-dom";

const NoStyleLink = styled(Link)`
    color: black;
`;

const HomeLink = ({ text, link }) => (
    <NoStyleLink to={link}>
        <DigitText.Text text={text} />
    </NoStyleLink>
);

export default HomeLink;
