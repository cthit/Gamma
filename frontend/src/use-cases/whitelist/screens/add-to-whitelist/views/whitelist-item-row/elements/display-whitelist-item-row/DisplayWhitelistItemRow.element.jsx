import React from "react";

import {
    DigitLayout,
    DigitText,
    DigitIconButton
} from "@cthit/react-digit-components";

import EditIcon from "@material-ui/icons/Edit";
import DeleteIcon from "@material-ui/icons/Delete";

const DisplayWhitelistItemRow = ({
    whitelistItem,
    startEditing,
    deleteWhitelistItem
}) => (
    <DigitLayout.Row fillElement justifyContent="space-between">
        <DigitLayout.Center>
            <DigitText.Text text={whitelistItem} />
        </DigitLayout.Center>
        <DigitLayout.Row centerVertical alignRight>
            <DigitLayout.Size width="48px" height="48px">
                <DigitIconButton onClick={startEditing} icon={EditIcon} />
            </DigitLayout.Size>
            <DigitLayout.Size width="48px" height="48px">
                <DigitIconButton
                    onClick={deleteWhitelistItem}
                    icon={DeleteIcon}
                />
            </DigitLayout.Size>
        </DigitLayout.Row>
    </DigitLayout.Row>
);

export default DisplayWhitelistItemRow;
