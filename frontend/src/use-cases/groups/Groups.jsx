import React, { useEffect, useState } from "react";

import { addDays } from "date-fns";

import {
    DigitCRUD,
    useDigitTranslations,
    DigitTextField,
    DigitButton,
    DigitSelect,
    DigitTextArea,
    DigitDatePicker
} from "@cthit/react-digit-components";
import translations from "./Groups.translations";
import { useDispatch } from "react-redux";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { getGroup, getGroupsMinified } from "../../api/groups/get.groups.api";
import {
    BECOMES_ACTIVE,
    BECOMES_INACTIVE,
    EMAIL,
    ID,
    NAME,
    PRETTY_NAME,
    SUPER_GROUP
} from "../../api/groups/props.groups.api";
import { editGroup } from "../../api/groups/put.groups.api";

import * as yup from "yup";
import { getSuperGroups } from "../../api/super-groups/get.super-groups.api";
import { addGroup } from "../../api/groups/post.groups.api";
import useIsAdmin from "../../common/hooks/use-is/use-is-admin";
import DisplayUsersTable from "../../common/elements/display-users-table";

const DESCRIPTION_SV = "descriptionSv";
const DESCRIPTION_EN = "descriptionEn";
const FUNCTION_SV = "functionSv";
const FUNCTION_EN = "functionEn";

function generateInitialValues() {
    const output = {};

    output[ID] = "";
    output[NAME] = "";
    output[EMAIL] = "";
    output[DESCRIPTION_SV] = "";
    output[DESCRIPTION_EN] = "";
    output[FUNCTION_SV] = "";
    output[FUNCTION_EN] = "";
    output[SUPER_GROUP] = "";
    output[PRETTY_NAME] = "";
    output[BECOMES_ACTIVE] = new Date();

    var aYearFromNow = new Date();
    aYearFromNow.setFullYear(aYearFromNow.getFullYear() + 1);
    output[BECOMES_INACTIVE] = aYearFromNow;

    return output;
}

function generateKeyTexts(text) {
    const output = {};

    output[ID] = text.Id;
    output[NAME] = text.Name;
    output[EMAIL] = text.Email;
    output[DESCRIPTION_SV] = text.DescriptionSv;
    output[DESCRIPTION_EN] = text.DescriptionEn;
    output[FUNCTION_SV] = text.FunctionSv;
    output[FUNCTION_EN] = text.FunctionEn;
    output[SUPER_GROUP] = text.SuperGroup;
    output[PRETTY_NAME] = text.PrettyName;
    output[BECOMES_ACTIVE] = text.BecomesActive;
    output[BECOMES_INACTIVE] = text.BecomesInactive;

    return output;
}

function generateValidationSchema(text) {
    const schema = {};

    schema[NAME] = yup.string().required();
    schema[PRETTY_NAME] = yup.string().required();
    schema[EMAIL] = yup.string().required();

    schema[DESCRIPTION_SV] = yup.string().required();
    schema[DESCRIPTION_EN] = yup.string().required();

    schema[FUNCTION_SV] = yup.string().required();
    schema[FUNCTION_EN] = yup.string().required();

    return yup.object().shape(schema);
}

function generateEditComponentData(text, superGroups = []) {
    const componentData = {};

    componentData[NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Name,
            maxLength: 50,
            outlined: true
        }
    };

    componentData[PRETTY_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.PrettyName,
            maxLength: 50,
            outlined: true
        }
    };

    componentData[DESCRIPTION_SV] = {
        component: DigitTextArea,
        componentProps: {
            upperLabel: text.DescriptionSv,
            maxLength: 500,
            rows: 5,
            maxRows: 10,
            outlined: true
        }
    };

    componentData[DESCRIPTION_EN] = {
        component: DigitTextArea,
        componentProps: {
            upperLabel: text.DescriptionEn,
            maxLength: 500,
            rows: 5,
            maxRows: 10,
            outlined: true
        }
    };

    componentData[EMAIL] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Email,
            maxLength: 100,
            outlined: true
        }
    };

    componentData[FUNCTION_SV] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.FunctionSv,
            maxLength: 100,
            outlined: true
        }
    };

    componentData[FUNCTION_EN] = {
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

    componentData[SUPER_GROUP] = {
        component: DigitSelect,
        componentProps: {
            upperLabel: text.SuperGroup,
            valueToTextMap: superGroupMap,
            outlined: true
        }
    };

    componentData[BECOMES_ACTIVE] = {
        component: DigitDatePicker,
        componentProps: {
            outlined: true
        }
    };

    componentData[BECOMES_INACTIVE] = {
        component: DigitDatePicker,
        componentProps: {
            outlined: true
        }
    };

    return componentData;
}

const Groups = ({ history }) => {
    const [text] = useDigitTranslations(translations);
    const dispatch = useDispatch();
    const admin = useIsAdmin();
    const [superGroups, setSuperGroups] = useState([]);

    useEffect(() => {
        dispatch(gammaLoadingFinished());
    }, [dispatch]);

    useEffect(() => {
        getSuperGroups().then(response => {
            setSuperGroups(response.data);
        });
    }, []);

    if (superGroups.length === 0) {
        return null;
    }

    return (
        <DigitCRUD
            name={"groups"}
            path={"/groups"}
            readAllRequest={getGroupsMinified}
            readOneRequest={getGroup}
            updateRequest={
                !admin
                    ? null
                    : (id, data) => {
                          const becomesActive = addDays(data.becomesActive, 1);
                          const becomesInactive = addDays(
                              data.becomesInactive,
                              1
                          );

                          return editGroup(id, {
                              name: data.name,
                              function: {
                                  sv: data.functionSv,
                                  en: data.functionEn
                              },
                              description: {
                                  sv: data.descriptionSv,
                                  en: data.descriptionEn
                              },
                              email: data.email,
                              superGroup: data.superGroup,
                              prettyName: data.prettyName,
                              becomesActive: becomesActive,
                              becomesInactive: becomesInactive
                          });
                      }
            }
            createRequest={data => {
                const becomesActive = addDays(data.becomesActive, 1);
                const becomesInactive = addDays(data.becomesInactive, 1);

                return addGroup({
                    name: data.name,
                    function: {
                        sv: data.functionSv,
                        en: data.functionEn
                    },
                    description: {
                        sv: data.descriptionSv,
                        en: data.descriptionEn
                    },
                    email: data.email,
                    superGroup: data.superGroup,
                    prettyName: data.prettyName,
                    becomesActive: becomesActive,
                    becomesInactive: becomesInactive
                });
            }}
            keysOrder={[
                ID,
                PRETTY_NAME,
                NAME,
                EMAIL,
                DESCRIPTION_SV,
                DESCRIPTION_EN,
                FUNCTION_SV,
                FUNCTION_EN,
                SUPER_GROUP,
                BECOMES_ACTIVE,
                BECOMES_INACTIVE
            ]}
            keysText={generateKeyTexts(text)}
            tableProps={{
                columnsOrder: [ID, NAME, EMAIL],
                orderBy: NAME,
                startOrderBy: NAME
            }}
            formInitialValues={generateInitialValues()}
            formValidationSchema={generateValidationSchema(text)}
            formComponentData={generateEditComponentData(text, superGroups)}
            idProp={"id"}
            detailsRenderCardEnd={data =>
                admin ? (
                    <>
                        <div style={{ marginTop: "8px" }} />
                        <DigitButton
                            outlined
                            text={"Edit members"}
                            onClick={() => history.push("/members/" + data.id)}
                        />
                    </>
                ) : null
            }
            detailsRenderEnd={data =>
                admin ? (
                    <div style={{ marginTop: "8px" }}>
                        <DisplayUsersTable
                            noUsersText={text.NoGroupMembers}
                            users={data.groupMembers}
                        />
                    </div>
                ) : null
            }
            dateProps={[BECOMES_ACTIVE, BECOMES_INACTIVE]}
        />
    );
};

export default Groups;
