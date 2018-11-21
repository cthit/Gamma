import { DigitEditData, DigitTextField } from "@cthit/react-digit-components";
import React from "react";
import * as yup from "yup";

const PostForm = ({
    initialValues,
    onSubmit,
    titleText,
    swedishInputText,
    englishInputText,
    submitText,
    fieldRequiredText
}) => (
    <DigitEditData
        initialValues={initialValues}
        onSubmit={(values, actions) => {
            const wrapped = {
                post: {
                    ...values
                }
            };
            onSubmit(wrapped, actions);
        }}
        validationSchema={yup.object().shape({
            sv: yup.string().required(fieldRequiredText),
            en: yup.string().required(fieldRequiredText)
        })}
        titleText={titleText}
        submitText={submitText}
        keysOrder={["sv", "en"]}
        keysComponentData={{
            sv: {
                component: DigitTextField,
                componentProps: {
                    upperLabel: swedishInputText
                }
            },
            en: {
                component: DigitTextField,
                componentProps: {
                    upperLabel: englishInputText
                }
            }
        }}
    />
);

export default PostForm;
