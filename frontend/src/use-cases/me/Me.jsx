import React, { useCallback, useEffect, useState } from "react";
import {
    FIRST_NAME,
    LAST_NAME,
    NICK,
    PASSWORD
} from "../../api/users/props.users.api";
import {
    useDigitTranslations,
    DigitCRUD,
    DigitTextField,
    DigitButton,
    DigitLayout,
    DigitDesign,
    useGammaUser,
    useGammaInvalidateMe
} from "@cthit/react-digit-components";
import translations from "./Me.translations.json";
import { editMe } from "../../api/me/put.me.api";
import {
    generateUserCustomDetailsRenders,
    generateUserEditComponentData,
    generateUserInitialValues,
    generateUserKeyOrder,
    generateUserKeysTexts,
    generateUserValidationSchema
} from "../../common/utils/generators/user-form.generator";
import { deleteMe } from "../../api/me/delete.me.api";
import * as yup from "yup";
import { Switch, Route } from "react-router-dom";
import MeChangePassword from "./screens/me-change-password";
import MeGroups from "./screens/me-groups";

const Me = () => {
    const [text] = useDigitTranslations(translations);
    const user = useGammaUser();
    const invalidateMe = useGammaInvalidateMe();

    const fullName = data =>
        data[FIRST_NAME] + " '" + data[NICK] + "' " + data[LAST_NAME];

    return (
        <Switch>
            <Route exact path={"/me/groups"} component={MeGroups} />
            <Route
                exact
                path={"/me/change-password"}
                component={MeChangePassword}
            />
            <Route
                render={() => (
                    <DigitCRUD
                        backFromReadOneLink={"/"}
                        name={"me"}
                        path={"/me"}
                        staticId={null}
                        readOnePath={""}
                        updatePath={"/edit"}
                        readOneRequest={() =>
                            new Promise(resolve => {
                                resolve({ data: user });
                            })
                        }
                        keysOrder={generateUserKeyOrder()}
                        updateRequest={(id, newData) =>
                            new Promise((resolve, reject) =>
                                editMe(newData)
                                    .then(response => {
                                        invalidateMe();
                                        resolve(response);
                                    })
                                    .catch(error => reject(error))
                            )
                        }
                        detailsRenderCardEnd={() => (
                            <DigitLayout.Padding>
                                <DigitLayout.Row justifyContent={"center"}>
                                    <DigitDesign.Link
                                        to={"/me/change-password"}
                                    >
                                        <DigitButton
                                            outlined
                                            text={text.ChangePassword}
                                        />
                                    </DigitDesign.Link>
                                    <DigitDesign.Link to={"/me/groups"}>
                                        <DigitButton
                                            outlined
                                            text={text.YourGroups}
                                        />
                                    </DigitDesign.Link>
                                </DigitLayout.Row>
                            </DigitLayout.Padding>
                        )}
                        customDetailsRenders={generateUserCustomDetailsRenders(
                            text,
                            true
                        )}
                        keysText={generateUserKeysTexts(text)}
                        formValidationSchema={generateUserValidationSchema(
                            text
                        )}
                        formComponentData={generateUserEditComponentData(text)}
                        formInitialValues={generateUserInitialValues()}
                        detailsTitle={data => fullName(data)}
                        updateTitle={data => fullName(data)}
                        deleteRequest={(_, form) =>
                            deleteMe(form).then(() => null)
                        }
                        dialogDeleteTitle={() => text.AreYouSure}
                        dialogDeleteDescription={() => text.AreYouReallySure}
                        dialogDeleteConfirm={() => text.Delete}
                        dialogDeleteCancel={() => text.Cancel}
                        deleteDialogFormKeysOrder={[PASSWORD]}
                        deleteDialogFormValidationSchema={() =>
                            yup.object().shape({
                                password: yup
                                    .string()
                                    .min(8)
                                    .required(text.YouMustEnterPassword)
                            })
                        }
                        deleteDialogFormInitialValues={{ password: "" }}
                        deleteDialogFormComponentData={{
                            password: {
                                component: DigitTextField,
                                componentProps: {
                                    upperLabel: text.Password,
                                    password: true,
                                    outlined: true
                                }
                            }
                        }}
                    />
                )}
            />
        </Switch>
    );
};

export default Me;
