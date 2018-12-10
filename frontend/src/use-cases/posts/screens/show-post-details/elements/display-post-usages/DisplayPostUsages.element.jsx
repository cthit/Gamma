import React from "react";

import { PRETTY_NAME } from "../../../../../../api/groups/props.groups.api";

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
                            key={usage.id}
                            text={usage.pretty_name}
                        />
                    );
                })}
            </DigitLayout.Column>
        )}
    />
);

export default DisplayPostUsages;
