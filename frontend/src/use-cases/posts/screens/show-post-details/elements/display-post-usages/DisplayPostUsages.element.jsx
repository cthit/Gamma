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
                    console.log("HEJHEJHEJHEJHEJHEJHEJEHJ");
                    console.log("HEJHEJHEJHEJHEJHEJHEJEHJ");
                    console.log("HEJHEJHEJHEJHEJHEJHEJEHJ");
                    console.log("HEJHEJHEJHEJHEJHEJHEJEHJ");
                    console.log("HEJHEJHEJHEJHEJHEJHEJEHJ");
                    console.log("HEJHEJHEJHEJHEJHEJHEJEHJ");
                    console.log("HEJHEJHEJHEJHEJHEJHEJEHJ");
                    console.log("HEJHEJHEJHEJHEJHEJHEJEHJ");
                    console.log("HEJHEJHEJHEJHEJHEJHEJEHJ");
                    console.log("HEJHEJHEJHEJHEJHEJHEJEHJ");
                    console.log("HEJHEJHEJHEJHEJHEJHEJEHJ");
                    console.log("HEJHEJHEJHEJHEJHEJHEJEHJ");
                    console.log(usage);
                    return <DigitText.Text text={usage.pretty_name} />;
                })}
            </DigitLayout.Column>
        )}
    />
);

export default DisplayPostUsages;
