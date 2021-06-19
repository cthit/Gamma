import * as yup from "yup";

import { DigitTextField } from "@cthit/react-digit-components";

import { USER_CID } from "api/users/props.users.api";

export const validationSchema = text => {
    const schema = {};
    schema[USER_CID] = yup.string().required(text.Cid + text.IsRequired);

    return yup.object().shape(schema);
};

export const initialValues = () => {
    const initialValues = {};
    initialValues[USER_CID] = "";

    return initialValues;
};

export const keysComponentData = text => {
    const keysComponentData = {};
    keysComponentData[USER_CID] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Cid,
            outlined: true,
            maxLength: 10
        }
    };

    return keysComponentData;
};

export const keysOrder = () => [USER_CID];
