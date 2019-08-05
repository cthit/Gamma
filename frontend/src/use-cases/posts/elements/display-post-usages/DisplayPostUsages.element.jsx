import React from "react";

import { DigitLayout, DigitText } from "@cthit/react-digit-components";
import { ID, PRETTY_NAME } from "../../../../api/groups/props.groups.api";

const DisplayPostUsages = ({ usages }) => (
    <DigitLayout.Column>
        {usages.map(usage => (
            <DigitText.Text key={usage[ID]} text={usage[PRETTY_NAME]} />
        ))}
    </DigitLayout.Column>
);

DisplayPostUsages.defaultProps = {
    usages: []
};

export default DisplayPostUsages;
