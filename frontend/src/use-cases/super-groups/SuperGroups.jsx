import React from "react";
import { SG_ID, SG_NAME } from "../../api/super-groups/props.super-groups.api";
import { useDigitTranslations, DigitCRUD } from "@cthit/react-digit-components";
import translations from "./SuperGroups.translations";
import {
    getSuperGroup,
    getSuperGroups,
    getSuperGroupSubGroups
} from "../../api/super-groups/get.super-groups.api";
import { addSuperGroup } from "../../api/super-groups/post.super-groups.api";
import { deleteSuperGroup } from "../../api/super-groups/delete.super-groups.api";
import { editSuperGroup } from "../../api/super-groups/put.super-groups.api";
import ShowSubGroups from "./elements/show-sub-groups";
import useGammaIsAdmin from "../../common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import FourOFour from "../four-o-four";
import FiveZeroZero from "../../app/elements/five-zero-zero";
import {
    initialValues,
    keysComponentData,
    keysOrder,
    keysText,
    validationSchema
} from "./SuperGroups.options";
import InsufficientAccess from "../../common/views/insufficient-access";

const SuperGroups = () => {
    const [text] = useDigitTranslations(translations);

    const admin = useGammaIsAdmin();

    return (
        <DigitCRUD
            keysOrder={keysOrder()}
            keysText={keysText(text)}
            formInitialValues={initialValues()}
            formComponentData={keysComponentData(text)}
            formValidationSchema={validationSchema(text)}
            name={"superGroups"}
            path={"/super-groups"}
            readAllRequest={getSuperGroups}
            readOneRequest={id =>
                Promise.all([getSuperGroup(id), getSuperGroupSubGroups(id)])
            }
            createRequest={admin ? addSuperGroup : null}
            deleteRequest={admin ? deleteSuperGroup : null}
            updateRequest={admin ? editSuperGroup : null}
            tableProps={{
                titleText: text.SuperGroups,
                startOrderBy: SG_NAME,
                search: true,
                flex: "1",
                startOrderByDirection: "asc",
                size: { minWidth: "288px" },
                padding: "0px",
                searchText: text.Search
            }}
            idProp={SG_ID}
            detailsRenderEnd={one => (
                <ShowSubGroups
                    margin={{ top: "16px" }}
                    subGroups={one.subGroups}
                />
            )}
            toastCreateSuccessful={data =>
                data[SG_NAME] + " " + text.WasCreatedSuccessfully
            }
            toastCreateFailed={() => text.FailedCreatingSuperGroup}
            toastDeleteSuccessful={data =>
                data[SG_NAME] + " " + text.WasDeletedSuccessfully
            }
            toastDeleteFailed={data =>
                text.SuperGroupDeletionFailed1 +
                " " +
                data[SG_NAME] +
                " " +
                text.SuperGroupDeletionFailed2
            }
            toastUpdateSuccessful={data =>
                data[SG_NAME] + " " + text.WasUpdatedSuccessfully
            }
            toastUpdateFailed={data =>
                text.SuperGroupUpdateFailed1 +
                " " +
                data[SG_NAME] +
                " " +
                text.SuperGroupUpdateFailed2
            }
            dialogDeleteCancel={() => text.Cancel}
            dialogDeleteConfirm={() => text.Delete}
            dialogDeleteTitle={() => text.AreYouSure}
            dialogDeleteDescription={data =>
                text.AreYouSureYouWantToDelete + " " + data[SG_NAME] + "?"
            }
            backButtonText={text.Back}
            detailsButtonText={text.Details}
            createTitle={text.CreateSuperGroup}
            createButtonText={text.CreateSuperGroup}
            updateTitle={data => text.Update + " " + data[SG_NAME]}
            updateButtonText={data => text.Update + " " + data[SG_NAME]}
            deleteButtonText={data => text.Delete + " " + data[SG_NAME]}
            detailsTitle={data => data[SG_NAME]}
            statusRenders={{
                403: () => <InsufficientAccess />,
                404: () => <FourOFour />,
                500: (error, reset) => <FiveZeroZero reset={reset} />
            }}
            useHistoryGoBackOnBack
        />
    );
};

export default SuperGroups;
