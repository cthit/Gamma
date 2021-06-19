import React, { useContext } from "react";
import { Link, useHistory } from "react-router-dom";
import styled from "styled-components";
import * as yup from "yup";

import {
    DigitButton,
    DigitCRUD,
    DigitLayout,
    DigitTextField,
    useDigitTranslations
} from "@cthit/react-digit-components";

import { deleteMe } from "api/me/delete.me.api";
import { editMe } from "api/me/put.me.api";
import {
    USER_FIRST_NAME,
    USER_LAST_NAME,
    USER_NICK,
    USER_PASSWORD
} from "api/users/props.users.api";

import GammaUserContext from "common/context/GammaUser.context";
import {
    generateUserCustomDetailsRenders,
    generateUserEditComponentData
} from "common/utils/generators/user-form.generator";
import InsufficientAccess from "common/views/insufficient-access";

import FiveZeroZero from "../../../../app/elements/five-zero-zero";
import FourOFour from "../../../four-o-four";
import {
    initialValues,
    keysOrder,
    keysText,
    validationSchema,
    updateKeysOrder
} from "../../Me.options";
import translations from "./MeCRUD.screen.translations";
import { getBackendUrl } from "../../../../common/utils/configs/envVariablesLoader";

const NoStyleLink = styled(Link)`
    color: inherit;
    text-decoration: none;
    display: flex;
    justify-content: center;
`;

const UserImage = styled.img`
    width: 250px;
    max-height: 500px;
    margin: auto;
`;

const MeCRUD = () => {
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
        <DigitCRUD
            keysOrder={keysOrder()}
            updateKeysOrder={updateKeysOrder()}
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
            detailsRenderCardStart={data => (
                <>
                    <UserImage
                        src={"/api/internal/users/avatar/" + data.id}
                        alt={"Profile picture"}
                    />
                    <NoStyleLink to={"/me/avatar"}>
                        <DigitButton outlined text={text.ChangeAvatar} />
                    </NoStyleLink>
                    <DigitLayout.Row
                        justifyContent={"center"}
                        flexWrap={"wrap"}
                    >
                        <NoStyleLink to={"/me/change-password"}>
                            <DigitButton outlined text={text.ChangePassword} />
                        </NoStyleLink>
                        <NoStyleLink to={"/me/groups"}>
                            <DigitButton outlined text={text.YourGroups} />
                        </NoStyleLink>
                    </DigitLayout.Row>
                </>
            )}
            customDetailsRenders={generateUserCustomDetailsRenders(text, true)}
            detailsTitle={data => text.YourInformation}
            updateTitle={data => fullName(data)}
            deleteRequest={(_, form) =>
                deleteMe(form).then(() => {
                    window.location.replace(
                        getBackendUrl() + "/account-deleted"
                    );
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
                        outlined: true,
                        autoComplete: false
                    }
                }
            }}
            statusRenders={{
                403: () => <InsufficientAccess />,
                404: () => <FourOFour />,
                500: (error, reset) => <FiveZeroZero reset={reset} />
            }}
            backButtonText={text.Back}
            deleteButtonText={() => text.DeleteMe}
            updateButtonText={() => text.EditMe}
            toastUpdateSuccessful={() => text.UpdateMe}
            toastUpdateFailed={() => text.UpdateMeFailed}
        />
    );
};

export default MeCRUD;
