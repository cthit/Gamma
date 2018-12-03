import React from "react";

import {
    DigitFormFieldArray,
    DigitLayout,
    DigitButton
} from "@cthit/react-digit-components";
import WhitelistItemRow from "../../views/whitelist-item-row";

const WhitelistItemArray = () => (
    <DigitFormFieldArray
        name="cids"
        render={({ form, remove, push, replace }) => (
            <DigitLayout.Column>
                {form.values.cids.map((whitelistItem, index) => (
                    <WhitelistItemRow
                        key={index}
                        whitelistItem={whitelistItem}
                        deleteWhitelistItem={() => {
                            remove(index);
                        }}
                        updateWhitelistItem={cid => {
                            replace(index, cid);
                        }}
                    />
                ))}
                <DigitButton
                    outlined
                    text="Add new whitelist item"
                    onClick={() => {
                        push("");
                    }}
                />
                {/* <DigitButton outlined text="Paste in whitelist items" /> */}
            </DigitLayout.Column>
        )}
    />
);

export default WhitelistItemArray;
