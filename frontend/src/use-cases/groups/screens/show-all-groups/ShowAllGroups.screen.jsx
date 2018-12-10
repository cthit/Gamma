import {
    DigitDesign,
    DigitFAB,
    DigitLayout,
    DigitTable,
    DigitTranslations
} from "@cthit/react-digit-components";
import { Add } from "@material-ui/icons";
import React, { Component } from "react";
import translations from "./ShowAllGroups.screen.translations.json";

class ShowAllGroups extends Component {
    componentDidMount() {
        const { groupsLoad, gammaLoadingFinished } = this.props;

        groupsLoad().then(() => {
            gammaLoadingFinished();
        });
    }

    componentWillUnmount() {
        this.props.gammaLoadingStart();
    }

    render() {
        let { groups } = this.props;
        return (
            <DigitTranslations
                translations={translations}
                uniquePath="Groups.Screen.ShowAllGroups"
                render={text => (
                    <DigitLayout.Fill>
                        <DigitTable
                            titleText={text.Groups}
                            searchText={text.SearchForGroups}
                            idProp="id"
                            startOrderBy="name"
                            columnsOrder={[
                                "id",
                                "name",
                                "description",
                                "email",
                                "func",
                                "type"
                            ]}
                            headerTexts={{
                                id: text.Id,
                                name: text.Name,
                                description: text.Description,
                                email: text.Email,
                                func: text.Function,
                                type: text.Type,
                                __link: text.Details
                            }}
                            data={groups.map(group => {
                                return {
                                    id: group.id,
                                    name: group.name,
                                    description: group.description.sv,
                                    email: group.email,
                                    func: group.func.sv,
                                    type: _getTypeText(group.type, text),
                                    __link: "/groups/" + group.id
                                };
                            })}
                            emptyTableText={text.NoGroups}
                        />
                        <DigitLayout.DownRightPosition>
                            <DigitDesign.Link to="/groups/new">
                                <DigitFAB icon={Add} secondary />
                            </DigitDesign.Link>
                        </DigitLayout.DownRightPosition>
                    </DigitLayout.Fill>
                )}
            />
        );
    }
}

function _getTypeText(type, text) {
    switch (type) {
        case "SOCIETY":
            return text.society;
        case "COMMITTEE":
            return text.Committee;
        case "BOARD":
            return text.Board;
        default:
            return "Unknown";
    }
}

export default ShowAllGroups;
