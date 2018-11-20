import React from "react";

import * as yup from "yup";

import {
    GammaCard,
    GammaCardBody,
    GammaCardButtons
} from "../../../../../common-ui/design";

import GammaEditData from "../../../../../common/elements/gamma-edit-data";
import GammaSelect from "../../../../../common/elements/gamma-select";
import {
    DigitTranslations,
    DigitTextField
} from "@cthit/react-digit-components";

import translations from "./GroupForm.view.translations";

const GroupForm = ({ initialValues, onSubmit }) => (
    <DigitTranslations
        translations={translations}
        uniquePath="Groups.Screen.GroupForm"
        render={text => (
            <GammaEditData
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
                        component: GammaSelect,
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
