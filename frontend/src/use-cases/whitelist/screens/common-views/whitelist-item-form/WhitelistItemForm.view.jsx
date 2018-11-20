import React from "react";
import * as yup from "yup";

import { DigitTextField, DigitEditData } from "@cthit/react-digit-components";

const WhitelistItemForm = ({
    onSubmit,
    initialValues,
    titleText,
    cidInputText,
    fieldRequiredText,
    submitText
}) => (
    <DigitEditData
        validationSchema={yup.object().shape({
            cid: yup.string().required(fieldRequiredText)
        })}
        initialValues={initialValues}
        onSubmit={onSubmit}
        titleText={titleText}
        submitText={submitText}
        keysOrder={["cid"]}
        keysComponentData={{
            cid: {
                component: DigitTextField,
                componentProps: {
                    upperLabel: cidInputText
                }
            }
        }}
    />
);

export default WhitelistItemForm;
