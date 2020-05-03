import * as yup from "yup";
import { DigitTextField } from "@cthit/react-digit-components";
import {
    ENGLISH_LANGUAGE,
    SWEDISH_LANGUAGE
} from "../../api/utils/commonProps";
import { POST_ENGLISH, POST_SWEDISH } from "../../api/posts/props.posts.api";

export const validationSchema = text => {
    const schema = {};

    schema[POST_SWEDISH] = yup
        .string()
        .required(text.SwedishInput + text.IsRequired);
    schema[POST_ENGLISH] = yup
        .string()
        .required(text.EnglishInput + text.IsRequired);

    return yup.object().shape(schema);
};

export const initialValues = () => {
    const output = {};

    output[POST_SWEDISH] = "";
    output[POST_ENGLISH] = "";

    return output;
};

export const keysComponentData = text => {
    const componentData = {};

    componentData[SWEDISH_LANGUAGE] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.SwedishInput,
            outlined: true,
            maxLength: 50
        }
    };

    componentData[ENGLISH_LANGUAGE] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.EnglishInput,
            outlined: true,
            maxLength: 50
        }
    };

    return componentData;
};

export const keysText = text => {
    const keysText = {};

    keysText[POST_SWEDISH] = text.Swedish;
    keysText[POST_ENGLISH] = text.English;

    return keysText;
};

export const keysOrder = () => [POST_SWEDISH, POST_ENGLISH];
