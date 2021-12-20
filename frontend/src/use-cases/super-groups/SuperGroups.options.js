import * as yup from "yup";

import { DigitSelect, DigitTextField } from "@cthit/react-digit-components";

import {
    SG_EN_TEXT,
    SG_NAME,
    SG_PRETTY_NAME,
    SG_SV_TEXT,
    SG_TYPE,
    SG_TYPE_SOCIETY
} from "api/super-groups/props.super-groups.api";

export const validationSchema = text => {
    const schema = {};
    schema[SG_NAME] = yup.string().required(text.Name + text.IsRequired);
    schema[SG_PRETTY_NAME] = yup
        .string()
        .required(text.PrettyName + text.IsRequired);
    schema[SG_TYPE] = yup.string().required(text.Type + text.IsRequired);
    schema[SG_SV_TEXT] = yup.string();
    schema[SG_EN_TEXT] = yup.string();

    return yup.object().shape(schema);
};

export const initialValues = () => {
    const output = {};

    output[SG_NAME] = "";
    output[SG_PRETTY_NAME] = "";
    output[SG_TYPE] = SG_TYPE_SOCIETY;
    output[SG_SV_TEXT] = "";
    output[SG_EN_TEXT] = "";

    return output;
};

export const keysComponentData = (text, superGroupTypes) => {
    const componentData = {};

    componentData[SG_NAME] = {
        component: DigitTextField,
        componentProps: {
            outlined: true,
            maxLength: 50
        }
    };

    componentData[SG_PRETTY_NAME] = {
        component: DigitTextField,
        componentProps: {
            outlined: true,
            maxLength: 50
        }
    };

    const typeValueToTextMap = {};

    for (let i = 0; i < superGroupTypes.length; i++) {
        let { type } = superGroupTypes[i];
        typeValueToTextMap[type] = type;
    }

    componentData[SG_TYPE] = {
        component: DigitSelect,
        componentProps: {
            valueToTextMap: typeValueToTextMap,
            outlined: true
        }
    };

    componentData[SG_SV_TEXT] = {
        component: DigitTextField,
        componentProps: {
            outlined: true,
            maxLength: 100
        }
    };

    componentData[SG_EN_TEXT] = {
        component: DigitTextField,
        componentProps: {
            outlined: true,
            maxLength: 100
        }
    };

    return componentData;
};

export const keysText = text => {
    const keysText = {};

    keysText[SG_NAME] = text.Name;
    keysText[SG_PRETTY_NAME] = text.PrettyName;
    keysText[SG_TYPE] = text.Type;
    keysText[SG_SV_TEXT] = text.DescriptionSv;
    keysText[SG_EN_TEXT] = text.DescriptionEn;

    return keysText;
};

export const keysOrder = () => [
    SG_NAME,
    SG_PRETTY_NAME,
    SG_TYPE,
    SG_SV_TEXT,
    SG_EN_TEXT
];
