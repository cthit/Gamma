import {
    DigitTextField,
    DigitTranslations,
    DigitEditData,
    DigitSelect
} from "@cthit/react-digit-components";
import React from "react";
import * as yup from "yup";
import translations from "./GroupForm.view.translations";

const GroupForm = ({ initialValues, onSubmit }) => (
    <DigitTranslations
        translations={translations}
        uniquePath="Groups.Screen.GroupForm"
        render={text => (
            <DigitEditData
                titleText={text.Group}
                submitText={text.SaveGroup}
                validationSchema={yup.object().shape({
                    name: yup.string().required(),
                    prettyName: yup.string().required(),
                    description: yup
                        .object()
                        .shape({
                            sv: yup.string().required(),
                            en: yup.string().required()
                        })
                        .required(),
                    email: yup.string().required(),
                    func: yup
                        .object()
                        .shape({
                            sv: yup.string().required(),
                            en: yup.string().required()
                        })
                        .required(),
                    type: yup.string().required()
                })}
                onSubmit={onSubmit}
                initialValues={initialValues}
                keysOrder={[
                    "name",
                    "prettyName",
                    "description.sv",
                    "description.en",
                    "email",
                    "func.sv",
                    "func.en",
                    "type"
                ]}
                keysComponentData={{
                    name: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.Name
                        }
                    },
                    prettyName: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.PrettyName
                        }
                    },
                    "description.sv": {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.DescriptionSv
                        }
                    },
                    "description.en": {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.DescriptionEn
                        }
                    },
                    email: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.Email
                        }
                    },
                    "func.sv": {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.FunctionSv
                        }
                    },
                    "func.en": {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.FunctionEn
                        }
                    },
                    type: {
                        component: DigitSelect,
                        componentProps: {
                            upperLabel: text.Type,
                            valueToTextMap: {
                                SOCIETY: text.Society,
                                COMMITTEE: text.Committee,
                                BOARD: text.Board
                            }
                        }
                    }
                }}
            />
        )}
    />
);

export default GroupForm;
