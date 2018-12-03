import React from "react";

import {
    DigitLayout,
    DigitTextField,
    DigitIconButton
} from "@cthit/react-digit-components";

import SaveIcon from "@material-ui/icons/Check";
import CancelIcon from "@material-ui/icons/Close";

class EditWhitelistItemRow extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            newWhitelistItem: props.whitelistItem
        };
    }

    onChange = e => {
        this.setState({
            newWhitelistItem: e.target.value
        });
    };

    render() {
        const { newWhitelistItem } = this.state;
        const { stopEditing, updateWhitelistItem } = this.props;

        return (
            <DigitLayout.Row jusitfyContent="space-between">
                <DigitTextField
                    value={newWhitelistItem}
                    onChange={this.onChange}
                />
                <DigitLayout.Row centerVertical>
                    <DigitLayout.Size width="48px" height="48px">
                        <DigitIconButton
                            icon={CancelIcon}
                            onClick={stopEditing}
                        />
                    </DigitLayout.Size>
                    <DigitLayout.Size width="48px" height="48px">
                        <DigitIconButton
                            icon={SaveIcon}
                            onClick={() => {
                                updateWhitelistItem(newWhitelistItem);
                                stopEditing();
                            }}
                        />
                    </DigitLayout.Size>
                </DigitLayout.Row>
            </DigitLayout.Row>
        );
    }
}
export default EditWhitelistItemRow;
