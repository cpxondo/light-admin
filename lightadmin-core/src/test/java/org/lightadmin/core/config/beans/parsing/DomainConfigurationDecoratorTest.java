package org.lightadmin.core.config.beans.parsing;

import org.easymock.EasyMock;
import org.junit.Test;
import org.lightadmin.core.config.beans.parsing.configuration.DomainConfigurationInterface;
import org.lightadmin.core.config.domain.filter.Filters;
import org.lightadmin.core.config.domain.fragment.FieldMetadata;
import org.lightadmin.core.config.domain.fragment.TableFragment;
import org.lightadmin.core.persistence.metamodel.DomainTypeEntityMetadata;

import java.util.Iterator;

import static org.easymock.EasyMock.eq;
import static org.junit.Assert.assertEquals;
import static org.lightadmin.core.test.util.DummyConfigurationsHelper.*;

public class DomainConfigurationDecoratorTest {

	private DomainConfigurationDecorator subject;

	@Test
	public void propertyFilterAppliedToFilters() throws Exception {
		final DomainTypeEntityMetadata domainTypeEntityMetadata = domainTypeEntityMetadataMock( DomainEntity.class );
		final DomainConfigurationInterface domainConfiguration = EasyMock.createMock( DomainConfigurationInterface.class );
		EasyMock.expect( domainConfiguration.getFilters() ).andReturn( filters( domainTypeEntityMetadata, "field1", "field2" ) ).once();
		EasyMock.replay( domainConfiguration );

		final ConfigurationUnitPropertyFilter propertyFilter = EasyMock.createMock( ConfigurationUnitPropertyFilter.class );
		EasyMock.expect( propertyFilter.apply( eq( "field1" ) ) ).andReturn( false ).once();
		EasyMock.expect( propertyFilter.apply( eq( "field2" ) ) ).andReturn( true ).once();
		EasyMock.replay( propertyFilter );

		subject = new DomainConfigurationDecorator( domainConfiguration, propertyFilter );

		final Filters filters = subject.getFilters();

		EasyMock.verify( domainConfiguration, propertyFilter );

		assertEquals( 1, filters.size() );
		assertEquals( "field2", filters.iterator().next().getFieldName() );
	}

	@Test
	public void propertyFilterAppliedToListView() {
		final DomainConfigurationInterface domainConfiguration = EasyMock.createMock( DomainConfigurationInterface.class );
		EasyMock.expect( domainConfiguration.getListViewFragment() ).andReturn( listView( "field1", "field2", "field3" ) ).once();
		EasyMock.replay( domainConfiguration );

		final ConfigurationUnitPropertyFilter propertyFilter = EasyMock.createMock( ConfigurationUnitPropertyFilter.class );
		EasyMock.expect( propertyFilter.apply( eq( "field1" ) ) ).andReturn( false ).once();
		EasyMock.expect( propertyFilter.apply( eq( "field2" ) ) ).andReturn( true ).once();
		EasyMock.expect( propertyFilter.apply( eq( "field3" ) ) ).andReturn( true ).once();
		EasyMock.replay( propertyFilter );

		subject = new DomainConfigurationDecorator( domainConfiguration, propertyFilter );

		final TableFragment listViewFragment = ( TableFragment ) subject.getListViewFragment();

		EasyMock.verify( domainConfiguration, propertyFilter );

		Iterator<FieldMetadata> columnsIterator = listViewFragment.getFields().iterator();

		assertEquals( 2, listViewFragment.getFields().size() );
		assertEquals( "field2", columnsIterator.next().getFieldName() );
		assertEquals( "field3", columnsIterator.next().getFieldName() );
	}
}