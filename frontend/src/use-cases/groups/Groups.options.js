import * as yup from "yup";
import {
    DigitDatePicker,
    DigitSelect,
    DigitTextArea,
    DigitTextField
} from "@cthit/react-digit-components";
import {
    GROUP_BECOMES_ACTIVE,
    GROUP_BECOMES_INACTIVE,
    GROUP_DESCRIPTION_EN,
    GROUP_DESCRIPTION_SV,
    GROUP_EMAIL,
    GROUP_FUNCTION_EN,
    GROUP_FUNCTION_SV,
    GROUP_ID,
    GROUP_NAME,
    GROUP_PRETTY_NAME,
    GROUP_SUPER_GROUP,
    GROUP_SUPER_GROUP_PRETTY_NAME
} from "../../api/groups/props.groups.api";

export const validationSchema = text => {
    const schema = {};

    schema[GROUP_NAME] = yup.string().required(text.Name + text.IsRequired);
    schema[GROUP_PRETTY_NAME] = yup
        .string()
        .required(text.PrettyName + text.IsRequired);
    schema[GROUP_EMAIL] = yup.string().required(text.Email + text.IsRequired);

    schema[GROUP_DESCRIPTION_SV] = yup.string();
    schema[GROUP_DESCRIPTION_EN] = yup.string();

    schema[GROUP_FUNCTION_SV] = yup.string();
    schema[GROUP_FUNCTION_EN] = yup.string();

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
    output[GROUP_DESCRIPTION_SV] = "";
    output[GROUP_DESCRIPTION_EN] = "";
    output[GROUP_FUNCTION_SV] = "";
    output[GROUP_FUNCTION_EN] = "";
    output[GROUP_SUPER_GROUP] = "";
    output[GROUP_PRETTY_NAME] = "";
    output[GROUP_BECOMES_ACTIVE] = new Date();

    var aYearFromNow = new Date();
    aYearFromNow.setFullYear(aYearFromNow.getFullYear() + 1);
    output[GROUP_BECOMES_INACTIVE] = aYearFromNow;

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

    componentData[GROUP_DESCRIPTION_SV] = {
        component: DigitTextArea,
        componentProps: {
            upperLabel: text.DescriptionSv,
            maxLength: 500,
            rows: 5,
            maxRows: 10,
            outlined: true
        }
    };

    componentData[GROUP_DESCRIPTION_EN] = {
        component: DigitTextArea,
        componentProps: {
            upperLabel: text.DescriptionEn,
            maxLength: 500,
            rows: 5,
            maxRows: 10,
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

    componentData[GROUP_FUNCTION_SV] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.FunctionSv,
            maxLength: 100,
            outlined: true
        }
    };

    componentData[GROUP_FUNCTION_EN] = {
        component: DigitTextField,
        componentProps: {
            maxLength: 100,
            upperLabel: text.FunctionEn,
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

    componentData[GROUP_BECOMES_ACTIVE] = {
        component: DigitDatePicker,
        componentProps: {
            upperLabel: text.BecomesActive,
            outlined: true
        }
    };

    componentData[GROUP_BECOMES_INACTIVE] = {
        component: DigitDatePicker,
        componentProps: {
            upperLabel: text.BecomesInactive,
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
    keysText[GROUP_DESCRIPTION_SV] = text.DescriptionSv;
    keysText[GROUP_DESCRIPTION_EN] = text.DescriptionEn;
    keysText[GROUP_FUNCTION_SV] = text.FunctionSv;
    keysText[GROUP_FUNCTION_EN] = text.FunctionEn;
    keysText[GROUP_SUPER_GROUP] = text.SuperGroup;
    keysText[GROUP_SUPER_GROUP_PRETTY_NAME] = text.SuperGroup;
    keysText[GROUP_PRETTY_NAME] = text.PrettyName;
    keysText[GROUP_BECOMES_ACTIVE] = text.BecomesActive;
    keysText[GROUP_BECOMES_INACTIVE] = text.BecomesInactive;

    return keysText;
};

export const keysOrder = () => [
    GROUP_PRETTY_NAME,
    GROUP_NAME,
    GROUP_EMAIL,
    GROUP_DESCRIPTION_SV,
    GROUP_DESCRIPTION_EN,
    GROUP_FUNCTION_SV,
    GROUP_FUNCTION_EN,
    GROUP_SUPER_GROUP,
    GROUP_BECOMES_ACTIVE,
    GROUP_BECOMES_INACTIVE
];

export const readOneKeysOrder = () => [
    GROUP_PRETTY_NAME,
    GROUP_NAME,
    GROUP_EMAIL,
    GROUP_DESCRIPTION_SV,
    GROUP_DESCRIPTION_EN,
    GROUP_FUNCTION_SV,
    GROUP_FUNCTION_EN,
    GROUP_SUPER_GROUP_PRETTY_NAME,
    GROUP_BECOMES_ACTIVE,
    GROUP_BECOMES_INACTIVE
];

export const readAllKeysOrder = () => [
    GROUP_NAME,
    GROUP_PRETTY_NAME,
    GROUP_EMAIL
];

export const updateKeysOrder = () => [
    GROUP_PRETTY_NAME,
    GROUP_DESCRIPTION_SV,
    GROUP_DESCRIPTION_EN,
    GROUP_FUNCTION_SV,
    GROUP_FUNCTION_EN,
    GROUP_SUPER_GROUP,
    GROUP_BECOMES_ACTIVE,
    GROUP_BECOMES_INACTIVE
];
