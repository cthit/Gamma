import React from "react";

import { PRETTY_NAME, ID } from "../../../../../../api/groups/props.groups.api";

import { DigitLayout, DigitText } from "@cthit/react-digit-components";

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
