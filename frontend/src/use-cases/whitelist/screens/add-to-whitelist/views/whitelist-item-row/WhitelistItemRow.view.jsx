import React from "react";

import {
    DigitIfElseRendering,
    DigitLayout
} from "@cthit/react-digit-components";
import EditWhitelistItemRow from "./sub-views/edit-whitelist-item-row";
import DisplayWhitelistItemRow from "./elements/display-whitelist-item-row";

class WhitelistItemRow extends React.Component {
    state = {
        editing: true
    };

    render() {
        const { editing } = this.state;
        const {
            whitelistItem,
            deleteWhitelistItem,
            updateWhitelistItem
        } = this.props;

        return (
            <DigitLayout.Size width="300px" height="100px">
                <DigitIfElseRendering
                    test={editing}
                    ifRender={() => (
                        <EditWhitelistItemRow
                            whitelistItem={whitelistItem}
                            updateWhitelistItem={updateWhitelistItem}
                            stopEditing={() => {
                                this.setState({ editing: false });
                            }}
                        />
                    )}
                    elseRender={() => (
                        <DisplayWhitelistItemRow
                            whitelistItem={whitelistItem}
                            deleteWhitelistItem={deleteWhitelistItem}
                            startEditing={() => {
                                this.setState({ editing: true });
                            }}
                        />
                    )}
                />
            </DigitLayout.Size>
        );
    }
}

export default WhitelistItemRow;
