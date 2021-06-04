import * as yup from "yup";

import { DigitSelect, DigitTextField } from "@cthit/react-digit-components";

import {
    GROUP_EMAIL,
    GROUP_ID,
    GROUP_NAME,
    GROUP_PRETTY_NAME,
    GROUP_SUPER_GROUP,
    GROUP_SUPER_GROUP_PRETTY_NAME
} from "api/groups/props.groups.api";

export const validationSchema = text => {
    const schema = {};

    schema[GROUP_NAME] = yup.string().required(text.Name + text.IsRequired);
    schema[GROUP_PRETTY_NAME] = yup
        .string()
        .required(text.PrettyName + text.IsRequired);
    schema[GROUP_EMAIL] = yup.string().required(text.Email + text.IsRequired);

    schema[GROUP_SUPER_GROUP] = yup
        .string()
        .required(text.GroupMustHaveSuperGroup);

    return yup.object().shape(schema);
};

export const initialValues = () => {
    const output = {};

    output[GROUP_ID] = "";
    output[GROUP_NAME] = "";
    output[GROUP_EMAIL] = "";
    output[GROUP_SUPER_GROUP] = "";
    output[GROUP_PRETTY_NAME] = "";

    return output;
};

export const keysComponentData = (text, superGroups = []) => {
    const componentData = {};

    componentData[GROUP_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Name,
            maxLength: 50,
            outlined: true
        }
    };

    componentData[GROUP_PRETTY_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.PrettyName,
            maxLength: 50,
            outlined: true
        }
    };

    componentData[GROUP_EMAIL] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Email,
            maxLength: 100,
            outlined: true
        }
    };

    const superGroupMap = {};
    for (let i = 0; i < superGroups.length; i++) {
        superGroupMap[superGroups[i].id] = superGroups[i].prettyName;
    }

    componentData[GROUP_SUPER_GROUP] = {
        component: DigitSelect,
        componentProps: {
            upperLabel: text.SuperGroup,
            valueToTextMap: superGroupMap,
            outlined: true
        }
    };

    return componentData;
};

export const keysText = text => {
    const keysText = {};

    keysText[GROUP_ID] = text.Id;
    keysText[GROUP_NAME] = text.Name;
    keysText[GROUP_EMAIL] = text.Email;
    keysText[GROUP_SUPER_GROUP] = text.SuperGroup;
    keysText[GROUP_SUPER_GROUP_PRETTY_NAME] = text.SuperGroup;
    keysText[GROUP_PRETTY_NAME] = text.PrettyName;

    return keysText;
};

export const keysOrder = () => [
    GROUP_PRETTY_NAME,
    GROUP_NAME,
    GROUP_EMAIL,
    GROUP_SUPER_GROUP
];

export const readOneKeysOrder = () => [
    GROUP_PRETTY_NAME,
    GROUP_NAME,
    GROUP_EMAIL,
    GROUP_SUPER_GROUP_PRETTY_NAME
];

export const readAllKeysOrder = () => [
    GROUP_NAME,
    GROUP_PRETTY_NAME,
    GROUP_EMAIL
];

export const updateKeysOrder = () => [
    GROUP_PRETTY_NAME,
    GROUP_EMAIL,
    GROUP_SUPER_GROUP
];
