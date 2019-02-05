import React from "react";

import { PRETTY_NAME, ID } from "../../../../../../api/groups/props.groups.api";

import {
    DigitLayout,
    DigitIfElseRendering,
    DigitText
} from "@cthit/react-digit-components";

const DisplayPostUsages = ({ usages }) => (
    <DigitIfElseRendering
        test={usages != null}
        ifRender={() => (
            <DigitLayout.Column>
                {usages.map(usage => {
                    return (
                        <DigitText.Text
                            key={usage[ID]}
                            text={usage[PRETTY_NAME]}
                        />
                    );
                })}
            </DigitLayout.Column>
        )}
    />
);

export default DisplayPostUsages;
