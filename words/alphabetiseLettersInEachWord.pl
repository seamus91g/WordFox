
use 5.010;
use strict;
use warnings;
use utf8;

my @saveWords;

my $count = 0;
open my $infile, '<', "google-books-common-words_filtered.txt";
while (<$infile>){	
	my $line = $_;
	chomp($line);
	my @String = split '', $line;
	@String = sort @String;

	my $wordAlph = join '', @String;
	push @saveWords, "$line $wordAlph" ;

	$count++;
}


close $infile;
open my $outfile, '>', "google-books-common-words_filtered_AlphabetisedLetters.txt";
foreach my $word (@saveWords){
	print $outfile "$word\n";
}
close $outfile;