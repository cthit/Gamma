import React, { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
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
    DigitDesign
} from "@cthit/react-digit-components";
import translations from "./Me.translations.json";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import { editMe } from "../../api/me/put.me.api";
import { getWebsites } from "../../api/websites/get.websites.api";
import {
    userLogout,
    userUpdateMe
} from "../../app/elements/user-information/UserInformation.element.action-creator";
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

const Me = () => {
    const [text] = useDigitTranslations(translations);
    const [websites, setWebsites] = useState(null);
    const dispatch = useDispatch();
    const me = useSelector(state => state.user);

    useEffect(() => {
        getWebsites().then(response => {
            setWebsites(response.data);
            dispatch(gammaLoadingFinished());
        });
    }, [dispatch]);

    if (!me.loaded || websites == null) {
        return null;
    }

    const fullName = data =>
        data[FIRST_NAME] + " '" + data[NICK] + "' " + data[LAST_NAME];

    return (
        <Switch>
            <Route path={"/me/change-password"} component={MeChangePassword} />
            <Route
                render={() => (
                    <DigitCRUD
                        backFromReadOneLink={"/"}
                        name={"me"}
                        path={"/me"}
                        staticId={null}
                        readOnePath={""}
                        updatePath={"/edit"}
                        readOneRequest={() => dispatch(userUpdateMe())}
                        keysOrder={generateUserKeyOrder()}
                        updateRequest={(id, newData) => editMe(newData)}
                        detailsRenderCardEnd={() => (
                            <DigitLayout.Center>
                                <DigitLayout.Padding />
                                <DigitLayout.Size absWidth={"220px"}>
                                    <DigitDesign.Link
                                        to={"/me/change-password"}
                                    >
                                        <DigitButton
                                            raised
                                            text={text.ChangePassword}
                                            onClick={() => console.log("Hj")}
                                        />
                                    </DigitDesign.Link>
                                </DigitLayout.Size>
                            </DigitLayout.Center>
                        )}
                        customDetailsRenders={generateUserCustomDetailsRenders(
                            text
                        )}
                        keysText={generateUserKeysTexts(text)}
                        formValidationSchema={generateUserValidationSchema(
                            text
                        )}
                        formComponentData={generateUserEditComponentData(
                            text,
                            websites
                        )}
                        formInitialValues={generateUserInitialValues()}
                        detailsTitle={data => fullName(data)}
                        updateTitle={data => fullName(data)}
                        deleteRequest={(_, form) =>
                            deleteMe(form).then(() => dispatch(userLogout()))
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
