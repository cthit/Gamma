import React, { useContext } from "react";
import {
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_NICK,
    USER_PASSWORD
} from "../../api/users/props.users.api";
import {
    useDigitTranslations,
    DigitCRUD,
    DigitTextField,
    DigitButton,
    DigitLayout
} from "@cthit/react-digit-components";
import translations from "./Me.translations.json";
import { editMe } from "../../api/me/put.me.api";
import {
    generateUserCustomDetailsRenders,
    generateUserEditComponentData
} from "../../common/utils/generators/user-form.generator";
import { deleteMe } from "../../api/me/delete.me.api";
import * as yup from "yup";
import { Switch, Route } from "react-router-dom";
import MeChangePassword from "./screens/me-change-password";
import MeGroups from "./screens/me-groups";
import MeAvatar from "./screens/me-avatar";
import FourOFour from "../four-o-four";
import FiveZeroZero from "../../app/elements/five-zero-zero";
import { Link, useHistory } from "react-router-dom";
import styled from "styled-components";
import GammaUserContext from "../../common/context/GammaUser.context";
import {
    initialValues,
    keysText,
    validationSchema,
    keysOrder
} from "./Me.options";
import InsufficientAccess from "../../common/views/insufficient-access";

const NoStyleLink = styled(Link)`
    color: inherit;
    text-decoration: none;
    display: flex;
    justify-content: center;
`;

const Me = () => {
    const [text] = useDigitTranslations(translations);
    const [user, update] = useContext(GammaUserContext);
    const history = useHistory();

    const fullName = data =>
        data[USER_FIRST_NAME] +
        " '" +
        data[USER_NICK] +
        "' " +
        data[USER_LAST_NAME];

    if (user == null) {
        return null;
    }

    return (
        <Switch>
            <Route exact path={"/me/groups"} component={MeGroups} />
            <Route exact path={"/me/avatar"} component={MeAvatar} />
            <Route
                exact
                path={"/me/change-password"}
                component={MeChangePassword}
            />
            <Route
                render={() => (
                    <DigitCRUD
                        keysOrder={keysOrder()}
                        keysText={keysText(text)}
                        formValidationSchema={validationSchema(text)}
                        formComponentData={generateUserEditComponentData(text)}
                        formInitialValues={initialValues()}
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
                        updateRequest={(id, newData) =>
                            new Promise((resolve, reject) =>
                                editMe(newData)
                                    .then(response => {
                                        resolve(response);
                                        update().then(() => {
                                            history.push("/me");
                                        });
                                    })
                                    .catch(error => reject(error))
                            )
                        }
                        detailsRenderCardEnd={() => (
                            <DigitLayout.Row
                                justifyContent={"center"}
                                flexWrap={"wrap"}
                            >
                                <NoStyleLink to={"/me/change-password"}>
                                    <DigitButton
                                        outlined
                                        text={text.ChangePassword}
                                    />
                                </NoStyleLink>
                                <NoStyleLink to={"/me/groups"}>
                                    <DigitButton
                                        outlined
                                        text={text.YourGroups}
                                    />
                                </NoStyleLink>
                                <NoStyleLink to={"/me/avatar"}>
                                    <DigitButton
                                        outlined
                                        text={text.YourAvatar}
                                    />
                                </NoStyleLink>
                            </DigitLayout.Row>
                        )}
                        customDetailsRenders={generateUserCustomDetailsRenders(
                            text,
                            true
                        )}
                        detailsTitle={data => fullName(data)}
                        updateTitle={data => fullName(data)}
                        deleteRequest={(_, form) =>
                            deleteMe(form).then(() => {
                                window.location.reload();
                            })
                        }
                        dialogDeleteTitle={() => text.AreYouSure}
                        dialogDeleteDescription={() => text.AreYouReallySure}
                        dialogDeleteConfirm={() => text.Delete}
                        dialogDeleteCancel={() => text.Cancel}
                        deleteDialogFormKeysOrder={[USER_PASSWORD]}
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
                        statusRenders={{
                            403: () => <InsufficientAccess />,
                            404: () => <FourOFour />,
                            500: (error, reset) => (
                                <FiveZeroZero reset={reset} />
                            )
                        }}
                        useHistoryGoBackOnBack={false}
                        backFromReadOnePath={() => "/"}
                    />
                )}
            />
        </Switch>
    );
};

export default Me;
