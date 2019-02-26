import React from "react";
import {
    DigitTable,
    DigitTranslations,
    DigitFAB,
    DigitLayout,
    DigitDesign,
    DigitIfElseRendering
} from "@cthit/react-digit-components";

import translations from "./ShowAllSuperGroups.screen.translations";
import { Add } from "@material-ui/icons";
import {
    ID,
    PRETTY_NAME,
    TYPE,
    NAME
} from "../../../../api/super-groups/props.super-groups.api";

class ShowAllSuperGroup extends React.Component {
    componentDidMount() {
        this.props
            .getSuperGroups()
            .then(() => this.props.gammaLoadingFinished());
    }

    static generateHeaderTexts(text) {
        const headerTexts = {};
        headerTexts[ID] = text.Id;
        headerTexts[NAME] = text.Name;
        headerTexts[PRETTY_NAME] = text.PrettyName;
        headerTexts[TYPE] = text.Type;
        headerTexts["__link"] = text.Details;
        return headerTexts;
    }

    render() {
        const { superGroups } = this.props;

        return (
            <DigitIfElseRendering
                test={superGroups != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        render={text => (
                            <React.Fragment>
                                <DigitTable
                                    titleText={text.Users}
                                    searchText={text.SearchForSuperGroups}
                                    idProp={ID}
                                    startOrderBy={NAME}
                                    columnsOrder={[ID, NAME, PRETTY_NAME, TYPE]}
                                    headerTexts={ShowAllSuperGroup.generateHeaderTexts(
                                        text
                                    )}
                                    data={superGroups.map(superGroup => {
                                        return {
                                            ...superGroup,
                                            __link:
                                                "/super-groups/" + superGroup.id
                                        };
                                    })}
                                    emptyTableText={text.NoSuperGroups}
                                />
                                <DigitLayout.DownRightPosition>
                                    <DigitDesign.Link to="/users/add">
                                        <DigitFAB icon={Add} secondary />
                                    </DigitDesign.Link>
                                </DigitLayout.DownRightPosition>
                            </React.Fragment>
                        )}
                    />
                )}
            />
        );
    }
}

export default ShowAllSuperGroup;
