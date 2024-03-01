import React from "react";

import { DigitLayout, DigitText } from "@cthit/react-digit-components";
import {
    GROUP_ID,
    GROUP_PRETTY_NAME
} from "../../../../api/groups/props.groups.api";

const DisplayPostUsages = ({ usages }) => (
    <DigitLayout.Column>
        {usages.map(usage => (
            <DigitText.Text
                key={usage[GROUP_ID]}
                text={usage[GROUP_PRETTY_NAME]}
            />
        ))}
    </DigitLayout.Column>
);

DisplayPostUsages.defaultProps = {
    usages: []
};

export default DisplayPostUsages;
