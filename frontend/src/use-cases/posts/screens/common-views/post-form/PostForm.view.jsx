import { DigitEditData, DigitTextField } from "@cthit/react-digit-components";
import React from "react";
import * as yup from "yup";

import {
    SWEDISH_LANGUAGE,
    ENGLISH_LANGUAGE
} from "../../../../../api/utils/commonProps";
import { POST } from "../../../../../api/posts/props.posts.api";

function generateValidationSchema(text) {
    const schema = {};

    schema[SWEDISH_LANGUAGE] = yup.string().required(text.fieldRequiredText);
    schema[ENGLISH_LANGUAGE] = yup.string().required(text.fieldRequiredText);

    return yup.object().shape(schema);
}

function generateEditComponentData(text) {
    const componentData = {};

    componentData[SWEDISH_LANGUAGE] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.swedishInputText
        }
    };

    componentData[ENGLISH_LANGUAGE] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.englishInputText
        }
    };

    return componentData;
}

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
            const data = {};
            data[POST] = values;

            onSubmit(data, actions);
        }}
        validationSchema={generateValidationSchema({
            fieldRequiredText: fieldRequiredText
        })}
        titleText={titleText}
        submitText={submitText}
        keysOrder={[SWEDISH_LANGUAGE, ENGLISH_LANGUAGE]}
        keysComponentData={generateEditComponentData({
            swedishInputText: swedishInputText,
            englishInputText: englishInputText
        })}
    />
);

export default PostForm;
