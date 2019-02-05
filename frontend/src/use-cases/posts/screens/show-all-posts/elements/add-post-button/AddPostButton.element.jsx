import {
    DigitFAB,
    DigitDesign,
    DigitLayout
} from "@cthit/react-digit-components";
import { Add } from "@material-ui/icons";
import React from "react";

const AddPostButton = () => (
    <DigitLayout.DownRightPosition>
        <DigitDesign.Link to="/posts/add">
            <DigitFAB icon={Add} secondary />
        </DigitDesign.Link>
    </DigitLayout.DownRightPosition>
);

export default AddPostButton;
