import * as yup from "yup";
import { DigitSelect, DigitTextField } from "@cthit/react-digit-components";

import {
    SG_EMAIL,
    SG_NAME,
    SG_PRETTY_NAME,
    SG_TYPE,
    SG_TYPE_ADMIN,
    SG_TYPE_ALUMNI,
    SG_TYPE_BOARD,
    SG_TYPE_COMMITTEE,
    SG_TYPE_FUNCTIONARIES,
    SG_TYPE_SOCIETY
} from "../../api/super-groups/props.super-groups.api";

export const validationSchema = text => {
    const schema = {};
    schema[SG_NAME] = yup.string().required(text.Name + text.IsRequired);
    schema[SG_PRETTY_NAME] = yup
        .string()
        .required(text.PrettyName + text.IsRequired);
    schema[SG_TYPE] = yup.string().required(text.Type + text.IsRequired);
    schema[SG_EMAIL] = yup
        .string()
        .email(text.FieldNotEmail)
        .required(text.Email + text.IsRequired);

    return yup.object().shape(schema);
};

export const initialValues = () => {
    const output = {};

    output[SG_NAME] = "";
    output[SG_PRETTY_NAME] = "";
    output[SG_TYPE] = SG_TYPE_SOCIETY;
    output[SG_EMAIL] = "";

    return output;
};

export const keysComponentData = text => {
    const componentData = {};

    componentData[SG_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Name,
            outlined: true,
            maxLength: 50
        }
    };

    componentData[SG_PRETTY_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.PrettyName,
            outlined: true,
            maxLength: 50
        }
    };

    componentData[SG_EMAIL] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Email,
            outlined: true,
            maxLength: 100
        }
    };

    const typeValueToTextMap = {};
    typeValueToTextMap[SG_TYPE_SOCIETY] = text.Society;
    typeValueToTextMap[SG_TYPE_COMMITTEE] = text.Committee;
    typeValueToTextMap[SG_TYPE_BOARD] = text.Board;
    typeValueToTextMap[SG_TYPE_ADMIN] = text.Admin;
    typeValueToTextMap[SG_TYPE_FUNCTIONARIES] = text.Functionaries;
    typeValueToTextMap[SG_TYPE_ALUMNI] = text.Alumni;

    componentData[SG_TYPE] = {
        component: DigitSelect,
        componentProps: {
            upperLabel: text.Type,
            valueToTextMap: typeValueToTextMap,
            outlined: true
        }
    };

    return componentData;
};

export const keysText = text => {
    const keysText = {};

    keysText[SG_NAME] = text.Name;
    keysText[SG_PRETTY_NAME] = text.PrettyName;
    keysText[SG_TYPE] = text.Type;
    keysText[SG_EMAIL] = text.Email;

    return keysText;
};

export const keysOrder = () => [SG_NAME, SG_PRETTY_NAME, SG_TYPE, SG_EMAIL];
