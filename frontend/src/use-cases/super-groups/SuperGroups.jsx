import React, { useEffect } from "react";
import {
    EMAIL,
    NAME,
    PRETTY_NAME,
    TYPE,
    TYPE_BOARD,
    TYPE_COMMITTEE,
    TYPE_SOCIETY
} from "../../api/super-groups/props.super-groups.api";
import * as yup from "yup";
import {
    DigitSelect,
    DigitTextField,
    useDigitTranslations,
    DigitCRUD
} from "@cthit/react-digit-components";
import translations from "./SuperGroups.translations";
import { useDispatch } from "react-redux";
import { gammaLoadingFinished } from "../../app/views/gamma-loading/GammaLoading.view.action-creator";
import {
    getSuperGroup,
    getSuperGroups,
    getSuperGroupSubGroups
} from "../../api/super-groups/get.super-groups.api";
import { addSuperGroup } from "../../api/super-groups/post.super-groups.api";
import { deleteSuperGroup } from "../../api/super-groups/delete.super-groups.api";
import { editSuperGroup } from "../../api/super-groups/put.super-groups.api";
import ShowSubGroups from "./elements/show-super-groups/ShowSuperGroups.element";
import useIsAdmin from "../../common/hooks/use-is-admin/use-is-admin";
import InsufficientAccess from "../../common/views/insufficient-access";

function generateValidationSchema(text) {
    const schema = {};
    schema[NAME] = yup.string().required(text.FieldRequired);
    schema[PRETTY_NAME] = yup.string().required(text.FieldRequired);
    schema[TYPE] = yup.string().required(text.FieldRequired);
    schema[EMAIL] = yup
        .string()
        .email(text.FieldNotEmail)
        .required(text.FieldRequired);

    return yup.object().shape(schema);
}

function generateEditComponentData(text) {
    const componentData = {};

    componentData[NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Name,
            outlined: true,
            maxLength: 50
        }
    };

    componentData[PRETTY_NAME] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.PrettyName,
            outlined: true,
            maxLength: 50
        }
    };

    componentData[EMAIL] = {
        component: DigitTextField,
        componentProps: {
            upperLabel: text.Email,
            outlined: true,
            maxLength: 100
        }
    };

    const typeValueToTextMap = {};
    typeValueToTextMap[TYPE_SOCIETY] = text.Society;
    typeValueToTextMap[TYPE_COMMITTEE] = text.Committee;
    typeValueToTextMap[TYPE_BOARD] = text.Board;

    componentData[TYPE] = {
        component: DigitSelect,
        componentProps: {
            upperLabel: text.Type,
            valueToTextMap: typeValueToTextMap,
            outlined: true
        }
    };

    return componentData;
}

const SuperGroups = () => {
    const [text] = useDigitTranslations(translations);
    const dispatch = useDispatch();
    useEffect(() => {
        dispatch(gammaLoadingFinished());
    }, []);

    const admin = useIsAdmin();
    if (!admin) {
        return <InsufficientAccess />;
    }

    return (
        <DigitCRUD
            name={"superGroups"}
            path={"/super-groups"}
            readAllRequest={getSuperGroups}
            readOneRequest={id =>
                Promise.all([getSuperGroup(id), getSuperGroupSubGroups(id)])
            }
            createRequest={addSuperGroup}
            deleteRequest={deleteSuperGroup}
            updateRequest={editSuperGroup}
            keysOrder={[NAME, PRETTY_NAME, TYPE, EMAIL]}
            keysText={{
                name: text.Name,
                prettyName: text.PrettyName,
                type: text.Type,
                email: text.Email
            }}
            formComponentData={generateEditComponentData(text)}
            formInitialValues={{
                name: "",
                prettyName: "",
                type: TYPE_SOCIETY,
                email: ""
            }}
            formValidationSchema={generateValidationSchema(text)}
            tableProps={{
                titleText: text.SuperGroups,
                search: true
            }}
            idProp={"id"}
            detailsRenderEnd={one => (
                <div
                    style={{
                        marginTop: "8px"
                    }}
                >
                    <ShowSubGroups subGroups={one.subGroups} />
                </div>
            )}
            toastCreateSuccessful={data =>
                data[NAME] + " " + text.WasCreatedSuccessfully
            }
            toastCreateFailed={() => text.FailedCreatingSuperGroup}
            toastDeleteSuccessful={data =>
                data[NAME] + " " + text.WasDeletedSuccessfully
            }
            toastDeleteFailed={data =>
                text.SuperGropuDeletionFailed1 +
                " " +
                data[NAME] +
                " " +
                text.SuperGropuDeletionFailed2
            }
            toastUpdateSuccessful={data =>
                data[NAME] + " " + text.WasUpdatedSuccessfully
            }
            toastUpdateFailed={data =>
                text.SuperGroupUpdateFailed1 +
                " " +
                data[NAME] +
                " " +
                text.SuperGroupUpdateFailed2
            }
            dialogDeleteCancel={() => text.Cancel}
            dialogDeleteConfirm={() => text.Confirm}
            dialogDeleteTitle={() => text.AreYouSure}
            dialogDeleteDescription={data =>
                text.AreYouSureYouWantToDelete + " " + data[NAME] + "?"
            }
            backButtonText={text.Back}
            detailsButtonText={text.Details}
            createTitle={text.CreateSuperGroup}
            createButtonText={text.CreateSuperGroup}
            updateTitle={data => text.Update + " " + data[NAME]}
            updateButtonText={data => text.Update + " " + data[NAME]}
            deleteButtonText={data => text.Delete + " " + data[NAME]}
            detailsTitle={data => data[NAME]}
        />
    );
};

export default SuperGroups;
