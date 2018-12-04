import React from "react";

import { DigitText } from "@cthit/react-digit-components";
import { Link } from "react-router-dom";

const HomeLink = ({ text, link }) => (
    <Link to={link == null ? "/hej" : ""}>
        <DigitText.Text text={text} />
    </Link>
);

export default HomeLink;
